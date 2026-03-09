<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — ${community.name}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/community.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragments/navbar.jsp" />

<main class="community-page">

    <!-- Flash notification banner -->
    <c:if test="${not empty flash}">
        <c:set var="flashType"  value="${fn:startsWith(flash, 'success') ? 'success' : 'error'}" />
        <c:set var="flashText"  value="${fn:substring(flash, 8, fn:length(flash))}" />
        <div class="flash-banner flash-${flashType}" id="flashBanner">
            ${flashText}
            <button class="flash-close" onclick="this.parentElement.remove()">×</button>
        </div>
    </c:if>

    <!-- ── Community header ─────────────────────────────────────────────── -->
    <div class="community-header">
        <div class="community-header-cover">
            <c:choose>
                <c:when test="${not empty community.coverImageUrl}">
                    <img src="${community.coverImageUrl}" alt="${community.name}" />
                </c:when>
                <c:otherwise>
                    <div class="community-cover-placeholder">
                        ${fn:toUpperCase(fn:substring(community.name, 0, 1))}
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="community-header-info">
            <div class="community-header-meta">
                <span class="community-visibility-badge ${community.visibility == 'PRIVATE' ? 'private' : 'public'}">
                    ${community.visibility == 'PRIVATE' ? '🔒 Private' : '🌐 Public'}
                </span>
                <h1 class="community-name">${community.name}</h1>
                <c:if test="${not empty community.description}">
                    <p class="community-desc">${community.description}</p>
                </c:if>
                <span class="community-member-count">${memberCount} member${memberCount != 1 ? 's' : ''}</span>
            </div>

            <div class="community-header-actions">
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/login" class="btn-main">Join Community</a>
                    </c:when>
                    <c:when test="${membership == null}">
                        <form action="${pageContext.request.contextPath}/community?id=${community.id}" method="post">
                            <input type="hidden" name="action" value="join" />
                            <button type="submit" class="btn-main">
                                ${community.visibility == 'PRIVATE' ? '✉ Request to Join' : '+ Join Community'}
                            </button>
                        </form>
                    </c:when>
                    <c:when test="${membership.role == 'PENDING'}">
                        <span class="btn-ghost pending-label">⏳ Request Pending</span>
                    </c:when>
                    <c:otherwise>
                        <span class="membership-badge ${membership.role == 'ADMIN' ? 'badge-admin' : 'badge-member'}">
                            ${membership.role == 'ADMIN' ? '★ Admin' : '✓ Member'}
                        </span>
                        <%-- Only show Leave if user is not the founding creator --%>
                        <c:if test="${membership.role != 'ADMIN' || community.creator.id != sessionScope.user.id}">
                            <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                  method="post" style="display:inline">
                                <input type="hidden" name="action" value="leave" />
                                <button type="submit" class="btn-ghost btn-leave"
                                        onclick="return confirm('Leave this community?')">Leave</button>
                            </form>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- ── Tab bar ───────────────────────────────────────────────────────── -->
    <div class="community-tabs" role="tablist">
        <button class="community-tab active" role="tab" onclick="showTab('posts', this)">
            📌 Posts
            <c:if test="${not empty communityPosts}">
                <span class="tab-count">${fn:length(communityPosts)}</span>
            </c:if>
        </button>
        <button class="community-tab" role="tab" onclick="showTab('chat', this)">💬 Chat</button>
        <button class="community-tab" role="tab" onclick="showTab('members', this)">
            👥 Members
            <span class="tab-count">${memberCount}</span>
        </button>
        <c:if test="${isAdmin}">
            <button class="community-tab" role="tab" onclick="showTab('requests', this)">
                📋 Requests
                <c:if test="${not empty pendingRequests}">
                    <span class="requests-badge">${fn:length(pendingRequests)}</span>
                </c:if>
            </button>
        </c:if>
    </div>

    <!-- ══════════════════════════════════════════════════════════════════════
         POSTS TAB
         ══════════════════════════════════════════════════════════════════ -->
    <div class="community-tab-panel" id="tab-posts" role="tabpanel">

        <c:if test="${isActiveMember}">
            <div class="community-post-actions-bar">
                <div class="action-toggles">
                    <button class="btn-toggle active" id="toggleUpload" onclick="togglePostMode('upload')">
                        ⬆ Upload Image
                    </button>
                    <button class="btn-toggle" id="toggleShare" onclick="togglePostMode('share')">
                        🔗 Share Existing Post
                    </button>
                </div>

                <%-- Upload new image directly to community --%>
                <form id="mode-upload"
                      action="${pageContext.request.contextPath}/community?id=${community.id}"
                      method="post" enctype="multipart/form-data"
                      class="community-upload-form">
                    <input type="hidden" name="action" value="upload_direct" />
                    <input type="text" name="title" placeholder="Give your post a title…" required
                           class="upload-title-input" />
                    <div class="file-input-wrapper">
                        <input type="file" name="image" accept="image/*" required
                               id="commUpload" onchange="updateFileName(this)" />
                        <label for="commUpload" id="fileLabel" class="file-label">
                            📷 Choose Image
                        </label>
                    </div>
                    <button type="submit" class="btn-main">Post to Community →</button>
                </form>

                <%-- Share an existing post by its numeric ID --%>
                <form id="mode-share"
                      action="${pageContext.request.contextPath}/community?id=${community.id}"
                      method="post"
                      class="share-post-form hidden">
                    <input type="hidden" name="action" value="post" />
                    <div class="share-post-row">
                        <input type="number" name="postId" placeholder="Enter Post ID (e.g. 42)…"
                               class="share-post-input" min="1" required />
                        <button type="submit" class="btn-main">Share →</button>
                    </div>
                    <p class="share-hint">You can find a post's ID in its URL: <code>/post?id=42</code></p>
                </form>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty communityPosts}">
                <div class="community-empty-state">
                    <div class="empty-icon">📭</div>
                    <h3>No posts yet</h3>
                    <p>${isActiveMember ? 'Be the first to share something!' : 'Join the community to start posting.'}</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="community-posts-grid">
                    <c:forEach var="cp" items="${communityPosts}">
                        <a href="${pageContext.request.contextPath}/post?id=${cp.post.id}"
                           class="community-post-card">
                            <div class="community-post-img">
                                <img src="${cp.post.imageUrl}" alt="${cp.post.title}" loading="lazy" />
                            </div>
                            <div class="community-post-meta">
                                <span class="community-post-title">${cp.post.title}</span>
                                <span class="community-post-shared">shared by @${cp.sharedBy.username}</span>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- ══════════════════════════════════════════════════════════════════════
         CHAT TAB
         ══════════════════════════════════════════════════════════════════ -->
    <div class="community-tab-panel hidden" id="tab-chat" role="tabpanel">
        <div class="community-chat">
            <div class="chat-header">
                <span class="chat-title">Community Chat</span>
                <span class="chat-live-indicator" id="chatStatus">● Live</span>
            </div>

            <div class="chat-messages" id="chatMessages">
                <c:choose>
                    <c:when test="${empty recentMessages}">
                        <div class="chat-empty" id="chatEmpty">
                            No messages yet — say hello! 👋
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="msg" items="${recentMessages}">
                            <div class="chat-message" data-id="${msg.id}">
                                <span class="chat-author">@${msg.author.username}</span>
                                <p class="chat-text">${fn:escapeXml(msg.text)}</p>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${isActiveMember}">
                <form class="chat-input-form"
                      action="${pageContext.request.contextPath}/community?id=${community.id}"
                      method="post">
                    <input type="hidden" name="action" value="message" />
                    <input type="hidden" name="via" value="fetch" />
                    <div class="chat-input-row">
                        <input type="text" name="text" id="chatInput"
                               placeholder="Say something…"
                               autocomplete="off" class="chat-input"
                               maxlength="1000" />
                        <button type="submit" class="chat-send-btn" title="Send">↑</button>
                    </div>
                </form>
            </c:if>
            <c:if test="${!isActiveMember}">
                <div class="chat-join-prompt">
                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/login">Login</a> to chat.
                        </c:when>
                        <c:otherwise>
                            Join this community to participate in chat.
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>

    <!-- ══════════════════════════════════════════════════════════════════════
         MEMBERS TAB
         ══════════════════════════════════════════════════════════════════ -->
    <div class="community-tab-panel hidden" id="tab-members" role="tabpanel">
        <c:choose>
            <c:when test="${empty members}">
                <div class="community-empty-state">No members to display.</div>
            </c:when>
            <c:otherwise>
                <div class="members-list">
                    <c:forEach var="member" items="${members}">
                        <div class="member-row">
                            <div class="member-info">
                                <span class="member-avatar">${fn:toUpperCase(fn:substring(member.user.username, 0, 1))}</span>
                                <span class="member-username">@${member.user.username}</span>
                            </div>
                            <span class="member-role-badge ${member.role == 'ADMIN' ? 'admin' : 'member'}">
                                ${member.role == 'ADMIN' ? '★ Admin' : 'Member'}
                            </span>
                            <c:if test="${isAdmin && member.user.id != sessionScope.user.id}">
                                <div class="member-actions">
                                    <c:if test="${member.role == 'MEMBER'}">
                                        <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                              method="post" style="display:inline">
                                            <input type="hidden" name="action" value="promote" />
                                            <input type="hidden" name="targetUserId" value="${member.user.id}" />
                                            <button type="submit" class="btn-action btn-promote">Make Admin</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${member.role != 'ADMIN'}">
                                        <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                              method="post" style="display:inline">
                                            <input type="hidden" name="action" value="kick" />
                                            <input type="hidden" name="targetUserId" value="${member.user.id}" />
                                            <button type="submit" class="btn-action btn-danger"
                                                    onclick="return confirm('Remove @${member.user.username}?')">Remove</button>
                                        </form>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- ══════════════════════════════════════════════════════════════════════
         REQUESTS TAB (admin only)
         ══════════════════════════════════════════════════════════════════ -->
    <c:if test="${isAdmin}">
        <div class="community-tab-panel hidden" id="tab-requests" role="tabpanel">
            <c:choose>
                <c:when test="${empty pendingRequests}">
                    <div class="community-empty-state">
                        <div class="empty-icon">✅</div>
                        <h3>No pending requests</h3>
                        <p>All caught up!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="members-list">
                        <c:forEach var="pending" items="${pendingRequests}">
                            <div class="member-row">
                                <div class="member-info">
                                    <span class="member-avatar">${fn:toUpperCase(fn:substring(pending.user.username, 0, 1))}</span>
                                    <span class="member-username">@${pending.user.username}</span>
                                </div>
                                <span class="member-role-badge pending">⏳ Pending</span>
                                <div class="member-actions">
                                    <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                          method="post" style="display:inline">
                                        <input type="hidden" name="action" value="approve" />
                                        <input type="hidden" name="targetUserId" value="${pending.user.id}" />
                                        <button type="submit" class="btn-action btn-approve">✓ Approve</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                          method="post" style="display:inline">
                                        <input type="hidden" name="action" value="kick" />
                                        <input type="hidden" name="targetUserId" value="${pending.user.id}" />
                                        <button type="submit" class="btn-action btn-danger">✕ Decline</button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

</main>

<script>
    // ── Constants injected from server ────────────────────────────────────
    const COMMUNITY_ID = '${community.id}';
    const CONTEXT      = '${pageContext.request.contextPath}';
    const IS_MEMBER    = ${isActiveMember};

    // ── Flash auto-dismiss ────────────────────────────────────────────────
    const flashBanner = document.getElementById('flashBanner');
    if (flashBanner) setTimeout(() => flashBanner.remove(), 5000);

    // ── Tab switching ─────────────────────────────────────────────────────
    function showTab(name, btn) {
        document.querySelectorAll('.community-tab-panel').forEach(p => p.classList.add('hidden'));
        document.querySelectorAll('.community-tab').forEach(b => b.classList.remove('active'));
        document.getElementById('tab-' + name).classList.remove('hidden');
        btn.classList.add('active');
        if (name === 'chat') {
            scrollChatToBottom();
            document.getElementById('chatInput') && document.getElementById('chatInput').focus();
        }
    }

    function scrollChatToBottom() {
        const msgs = document.getElementById('chatMessages');
        if (msgs) msgs.scrollTop = msgs.scrollHeight;
    }

    // ── Live chat via SSE ─────────────────────────────────────────────────
    const renderedMsgIds = new Set();
    document.querySelectorAll('.chat-message[data-id]').forEach(el => {
        renderedMsgIds.add(el.dataset.id);
    });

    let sseRetryDelay = 2000;

    function startChatStream() {
        const es = new EventSource(CONTEXT + '/community/chat?id=' + COMMUNITY_ID);
        const statusEl = document.getElementById('chatStatus');

        es.onopen = () => {
            sseRetryDelay = 2000;
            if (statusEl) { statusEl.textContent = '● Live'; statusEl.className = 'chat-live-indicator'; }
        };

        es.onmessage = function(event) {
            const parts = event.data.split('|');
            if (parts.length < 3) return;
            const id       = parts[0];
            const username = parts[1];
            const text     = parts.slice(2).join('|'); // text may contain pipes
            if (renderedMsgIds.has(id)) return;
            renderedMsgIds.add(id);
            appendMessage(id, username, text);
        };

        es.onerror = function() {
            es.close();
            if (statusEl) { statusEl.textContent = '○ Reconnecting…'; statusEl.className = 'chat-live-indicator disconnected'; }
            setTimeout(startChatStream, sseRetryDelay);
            sseRetryDelay = Math.min(sseRetryDelay * 2, 30000); // exponential back-off, max 30s
        };
    }

    function appendMessage(id, username, text) {
        const container = document.getElementById('chatMessages');
        const empty     = document.getElementById('chatEmpty');
        if (empty) empty.remove();

        const msg = document.createElement('div');
        msg.className  = 'chat-message';
        msg.dataset.id = id;
        msg.innerHTML  =
            '<span class="chat-author">@' + escapeHtml(username) + '</span>' +
            '<p class="chat-text">'        + escapeHtml(text)     + '</p>';
        container.appendChild(msg);

        // Auto-scroll only when the chat panel is visible
        const chatPanel = document.getElementById('tab-chat');
        if (chatPanel && !chatPanel.classList.contains('hidden')) {
            scrollChatToBottom();
        }
    }

    function escapeHtml(str) {
        const d = document.createElement('div');
        d.appendChild(document.createTextNode(String(str)));
        return d.innerHTML;
    }

    // ── Chat form — submit via fetch (no page reload) ─────────────────────
    const chatForm = document.querySelector('.chat-input-form');
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const input = chatForm.querySelector('.chat-input');
            const text  = input.value.trim();
            if (!text) return;

            const sendBtn = chatForm.querySelector('.chat-send-btn');
            sendBtn.disabled = true;

            fetch(chatForm.action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(chatForm))
            })
            .then(res => {
                if (res.ok || res.status === 204) {
                    input.value = '';
                    input.focus();
                }
            })
            .catch(() => chatForm.submit())   // graceful fallback if fetch fails
            .finally(() => { sendBtn.disabled = false; });
        });

        // Send on Enter, Shift+Enter for newline
        document.getElementById('chatInput') &&
        document.getElementById('chatInput').addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                chatForm.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
            }
        });
    }

    // ── Post mode toggle ──────────────────────────────────────────────────
    function togglePostMode(mode) {
        const uploadForm  = document.getElementById('mode-upload');
        const shareForm   = document.getElementById('mode-share');
        const btnUpload   = document.getElementById('toggleUpload');
        const btnShare    = document.getElementById('toggleShare');

        if (mode === 'upload') {
            uploadForm.classList.remove('hidden');
            shareForm.classList.add('hidden');
            btnUpload.classList.add('active');
            btnShare.classList.remove('active');
        } else {
            uploadForm.classList.add('hidden');
            shareForm.classList.remove('hidden');
            btnUpload.classList.remove('active');
            btnShare.classList.add('active');
            shareForm.querySelector('.share-post-input') &&
            shareForm.querySelector('.share-post-input').focus();
        }
    }

    function updateFileName(input) {
        const label = document.getElementById('fileLabel');
        if (input.files && input.files[0]) {
            label.textContent = '✓ ' + input.files[0].name;
            label.style.borderColor = 'var(--ember)';
            label.style.color       = 'var(--ember)';
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────
    startChatStream();
    scrollChatToBottom();
</script>

</body>
</html>
