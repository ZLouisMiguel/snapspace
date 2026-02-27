<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — Upload</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/feed">Feed</a>
    </div>
</nav>

<main class="upload-page">

    <div class="upload-left">
        <div class="upload-header">
            <span class="upload-tag">Share your world</span>
            <h1 class="upload-headline">DROP<br>YOUR<br><span class="accent">SHOT</span></h1>
            <p class="upload-sub">
                Upload anything worth sharing.
                It goes straight to Cloudinary and
                lands on the feed instantly.
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/feed" class="btn-ghost">← Back to feed</a>
    </div>

    <div class="upload-right">
        <form class="upload-form" action="${pageContext.request.contextPath}/upload"
              method="post" enctype="multipart/form-data">

            <!-- Drop zone -->
            <label class="upload-dropzone" for="imageInput" id="dropzone">
                <div class="dropzone-idle" id="dropzoneIdle">
                    <div class="dropzone-icon">↑</div>
                    <p class="dropzone-label">Click to select or drag an image here</p>
                    <p class="dropzone-hint">JPG, PNG, HEIC — up to 5MB</p>
                </div>
                <div class="dropzone-preview" id="dropzonePreview">
                    <img id="previewImg" src="" alt="preview" />
                    <button type="button" class="dropzone-clear" id="clearBtn">✕ Remove</button>
                </div>
            </label>

            <input
                type="file"
                id="imageInput"
                name="image"
                accept="image/*"
                required
                style="display: none;"
            />

            <button type="submit" class="btn-main upload-submit" id="submitBtn" disabled>
                Upload to feed →
            </button>

        </form>
    </div>

</main>

<script>
    const input      = document.getElementById('imageInput');
    const dropzone   = document.getElementById('dropzone');
    const idle       = document.getElementById('dropzoneIdle');
    const preview    = document.getElementById('dropzonePreview');
    const previewImg = document.getElementById('previewImg');
    const clearBtn   = document.getElementById('clearBtn');
    const submitBtn  = document.getElementById('submitBtn');

    function showPreview(file) {
        const reader = new FileReader();
        reader.onload = e => {
            previewImg.src = e.target.result;
            idle.style.display = 'none';
            preview.style.display = 'flex';
            submitBtn.disabled = false;
            dropzone.classList.add('has-preview');
        };
        reader.readAsDataURL(file);
    }

    function clearPreview() {
        input.value = '';
        previewImg.src = '';
        idle.style.display = 'flex';
        preview.style.display = 'none';
        submitBtn.disabled = true;
        dropzone.classList.remove('has-preview');
    }

    input.addEventListener('change', () => {
        if (input.files[0]) showPreview(input.files[0]);
    });

    clearBtn.addEventListener('click', e => {
        e.preventDefault();
        clearPreview();
    });

    // Drag and drop
    dropzone.addEventListener('dragover', e => {
        e.preventDefault();
        dropzone.classList.add('drag-over');
    });

    dropzone.addEventListener('dragleave', () => {
        dropzone.classList.remove('drag-over');
    });

    dropzone.addEventListener('drop', e => {
        e.preventDefault();
        dropzone.classList.remove('drag-over');
        const file = e.dataTransfer.files[0];
        if (file && file.type.startsWith('image/')) {
            const dt = new DataTransfer();
            dt.items.add(file);
            input.files = dt.files;
            showPreview(file);
        }
    });
</script>

</body>
</html>