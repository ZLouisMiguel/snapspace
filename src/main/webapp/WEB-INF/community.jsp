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

    <!-- Community header -->
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
                <span class="community-member-count">${memberCount} members</span>
            </div>

            <!-- Join / Leave / Pending actions -->
            <div class="community-header-actions">
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/login" class="btn-main">Join</a>
                    </c:when>
                    <c:when test="${membership == null}">
                        <form action="${pageContext.request.contextPath}/community?id=${community.id}" method="post">
                            <input type="hidden" name="action" value="join" />
                            <button type="submit" class="btn-main">
                                ${community.visibility == 'PRIVATE' ? 'Request to Join' : 'Join'}
                            </button>
                        </form>
                    </c:when>
                    <c:when test="${membership.role == 'PENDING'}">
                        <span class="btn-ghost pending-label">⏳ Request Pending</span>
                    </c:when>
                    <c:otherwise>
                        <span class="membership-badge">${membership.role == 'ADMIN' ? '★ Admin' : '✓ Member'}</span>
                        <c:if test="${membership.role != 'ADMIN' || community.creator.id != sessionScope.user.id}">
                            <form action="${pageContext.request.contextPath}/community?id=${community.id}" method="post"
                                  style="display:inline">
                                <input type="hidden" name="action" value="leave" />
                                <button type="submit" class="btn-ghost"
                                        onclick="return confirm('Leave this community?')">Leave</button>
                            </form>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Tabs: Posts | Chat | Members | (Admin) Requests -->
    <div class="community-tabs">
        <button class="community-tab active" onclick="showTab('posts', this)">Posts</button>
        <button class="community-tab" onclick="showTab('chat', this)">Chat</button>
        <button class="community-tab" onclick="showTab('members', this)">Members</button>
        <c:if test="${isAdmin}">
            <button class="community-tab" onclick="showTab('requests', this)">
                Requests
                <c:if test="${not empty pendingRequests}">
                    <span class="requests-badge">${fn:length(pendingRequests)}</span>
                </c:if>
            </button>
        </c:if>
    </div>

    <!-- ── Posts tab ───────────────────────────────────────────────────── -->
    <div class="community-tab-panel" id="tab-posts">
        <c:if test="${isActiveMember}">
            <div class="community-post-actions-bar">
                <div class="action-toggles">
                    <button class="btn-sm btn-ghost active" onclick="togglePostMode('upload')">Upload New</button>
                    <button class="btn-sm btn-ghost" onclick="togglePostMode('share')">Share Existing</button>
                </div>

                <form id="mode-upload" action="${pageContext.request.contextPath}/community?id=${community.id}"
                      method="post" enctype="multipart/form-data" class="community-upload-form">
                    <input type="hidden" name="action" value="upload_direct" />
                    <input type="text" name="title" placeholder="Give your post a title..." required />
                    <div class="file-input-wrapper">
                        <input type="file" name="image" accept="image/*" required id="commUpload" onchange="updateFileName(this)"/>
                        <label for="commUpload" id="fileLabel">Choose Image</label>
                    </div>
                    <button type="submit" class="btn-main">Post to Community →</button>
                </form>

                <form id="mode-share" action="${pageContext.request.contextPath}/community?id=${community.id}"
                      method="post" class="share-post-form hidden">
                    <input type="hidden" name="action" value="post" />
                    <input type="number" name="postId" placeholder="Paste Post ID (e.g. 102)..." class="share-post-input" />
                    <button type="submit" class="btn-main">Share →</button>
                </form>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty communityPosts}">
                <div class="community-empty-state">No posts shared yet.</div>
            </c:when>
            <c:otherwise>
                <div class="community-posts-grid">
                    <c:forEach var="cp" items="${communityPosts}">
                        <a href="${pageContext.request.contextPath}/post?id=${cp.post.id}"
                           class="community-post-card">
                            <div class="community-post-img">
                                <img src="${cp.post.imageUrl}" alt="${cp.post.title}" />
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

    <!-- ── Chat tab ───────────────────────────────────────────────────── -->
    <div class="community-tab-panel hidden" id="tab-chat">
        <div class="community-chat">
            <div class="chat-messages" id="chatMessages">
                <c:choose>
                    <c:when test="${empty recentMessages}">
                        <div class="chat-empty">No messages yet. Say hello!</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="msg" items="${recentMessages}">
                            <div class="chat-message" data-id="${msg.id}">
                                <span class="chat-author">@${msg.author.username}</span>
                                <p class="chat-text">${msg.text}</p>
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
                        <input type="text" name="text" placeholder="Say something..."
                               autocomplete="off" class="chat-input" />
                        <button type="submit" class="chat-send-btn">↑</button>
                    </div>
                </form>
            </c:if>
            <c:if test="${!isActiveMember}">
                <div class="chat-join-prompt">
                    Join this community to participate in chat.
                </div>
            </c:if>
        </div>
    </div>

    <!-- ── Members tab ───────────────────────────────────────────────── -->
    <div class="community-tab-panel hidden" id="tab-members">
        <div class="members-list">
            <c:forEach var="member" items="${members}">
                <div class="member-row">
                    <span class="member-username">@${member.user.username}</span>
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
                                    <button type="submit" class="btn-ghost btn-sm">Make Admin</button>
                                </form>
                            </c:if>
                            <c:if test="${member.role != 'ADMIN'}">
                                <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                      method="post" style="display:inline">
                                    <input type="hidden" name="action" value="kick" />
                                    <input type="hidden" name="targetUserId" value="${member.user.id}" />
                                    <button type="submit" class="btn-ghost btn-sm btn-danger"
                                            onclick="return confirm('Remove this member?')">Remove</button>
                                </form>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- ── Requests tab (admin only) ─────────────────────────────────── -->
    <c:if test="${isAdmin}">
        <div class="community-tab-panel hidden" id="tab-requests">
            <c:choose>
                <c:when test="${empty pendingRequests}">
                    <div class="community-empty-state">No pending join requests.</div>
                </c:when>
                <c:otherwise>
                    <div class="members-list">
                        <c:forEach var="pending" items="${pendingRequests}">
                            <div class="member-row">
                                <span class="member-username">@${pending.user.username}</span>
                                <span class="member-role-badge pending">⏳ Pending</span>
                                <div class="member-actions">
                                    <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                          method="post" style="display:inline">
                                        <input type="hidden" name="action" value="approve" />
                                        <input type="hidden" name="targetUserId" value="${pending.user.id}" />
                                        <button type="submit" class="btn-main btn-sm">Approve</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/community?id=${community.id}"
                                          method="post" style="display:inline">
                                        <input type="hidden" name="action" value="kick" />
                                        <input type="hidden" name="targetUserId" value="${pending.user.id}" />
                                        <button type="submit" class="btn-ghost btn-sm btn-danger">Decline</button>
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
    const COMMUNITY_ID = '${community.id}';
    const CONTEXT      = '${pageContext.request.contextPath}';
    const IS_MEMBER    = ${isActiveMember};

    // ── Tab switching ──────────────────────────────────────────────────────
    function showTab(name, btn) {
        document.querySelectorAll('.community-tab-panel').forEach(p => p.classList.add('hidden'));
        document.querySelectorAll('.community-tab').forEach(b => b.classList.remove('active'));
        document.getElementById('tab-' + name).classList.remove('hidden');
        btn.classList.add('active');

        if (name === 'chat') scrollChatToBottom();
    }

    function scrollChatToBottom() {
        const msgs = document.getElementById('chatMessages');
        if (msgs) msgs.scrollTop = msgs.scrollHeight;
    }

    // ── Live chat via SSE ──────────────────────────────────────────────────
    const renderedMsgIds = new Set();
    document.querySelectorAll('.chat-message[data-id]').forEach(el => {
        renderedMsgIds.add(el.dataset.id);
    });

    function startChatStream() {
        const es = new EventSource(CONTEXT + '/community/chat?id=' + COMMUNITY_ID);

        es.onmessage = function(event) {
            const parts = event.data.split('|');
            if (parts.length < 3) return;
            const id       = parts[0];
            const username = parts[1];
            const text     = parts.slice(2).join('|');
            if (renderedMsgIds.has(id)) return;
            renderedMsgIds.add(id);
            appendMessage(id, username, text);
        };

        es.onerror = function() {
            es.close();
            setTimeout(startChatStream, 5000);
        };
    }

    function appendMessage(id, username, text) {
        const container = document.getElementById('chatMessages');
        const empty = container.querySelector('.chat-empty');
        if (empty) empty.remove();

        const msg = document.createElement('div');
        msg.className  = 'chat-message';
        msg.dataset.id = id;
        msg.innerHTML  =
            '<span class="chat-author">@' + escapeHtml(username) + '</span>' +
            '<p class="chat-text">'        + escapeHtml(text)     + '</p>';
        container.appendChild(msg);

        // Only auto-scroll if chat tab is visible
        const chatPanel = document.getElementById('tab-chat');
        if (chatPanel && !chatPanel.classList.contains('hidden')) {
            scrollChatToBottom();
        }
    }

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    }

    // ── Chat form — submit via fetch so page doesn't reload ────────────────
    const chatForm = document.querySelector('.chat-input-form');
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const input = chatForm.querySelector('.chat-input');
            const text  = input.value.trim();
            if (!text) return;
            fetch(chatForm.action, {
                method: 'POST',
                body: new URLSearchParams(new FormData(chatForm))
            }).then(() => {
                input.value = '';
                input.focus();
            }).catch(() => chatForm.submit());
        });
    }

    startChatStream();
    scrollChatToBottom();

    function togglePostMode(mode) {
        const uploadForm = document.getElementById('mode-upload');
        const shareForm = document.getElementById('mode-share');
        const buttons = document.querySelectorAll('.action-toggles button');

        if (mode === 'upload') {
            uploadForm.classList.remove('hidden');
            shareForm.classList.add('hidden');
            buttons[0].classList.add('active');
            buttons[1].classList.remove('active');
        } else {
            uploadForm.classList.add('hidden');
            shareForm.classList.remove('hidden');
            buttons[0].classList.remove('active');
            buttons[1].classList.add('active');
        }
    }

    function updateFileName(input) {
        const label = document.getElementById('fileLabel');
        if (input.files && input.files[0]) {
            label.innerText = input.files[0].name;
            label.style.borderColor = 'var(--ember)';
            label.style.color = 'var(--ember)';
        }
    }
</script>

</body>
</html>