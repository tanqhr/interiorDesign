const input = document.getElementById("images");
const preview = document.getElementById("imagePreview");

let selectedFiles = [];
const MAX_IMAGES = 10;

input.addEventListener("change", () => {

    for (const file of input.files) {
        if (selectedFiles.length >= MAX_IMAGES) {
            alert("Можете да качите максимум 10 снимки.");
            break;
        }

        const exists = selectedFiles.some(f =>
            f.name === file.name &&
            f.size === file.size
        );

        if (!exists) {
            selectedFiles.push(file);
        }
    }

    renderImages();
});

function renderImages() {

    preview.innerHTML = "";

    const dataTransfer = new DataTransfer();

    selectedFiles.forEach((file, index) => {

        dataTransfer.items.add(file);

        const reader = new FileReader();

        reader.onload = function (e) {

            const col = document.createElement("div");

            col.className = "col-md-3";

            col.innerHTML = `
                <div class="preview-card">

                    <img src="${e.target.result}" alt="Preview">

                    <button
                        type="button"
                        class="remove-image"
                        data-index="${index}">

                        ×

                    </button>

                </div>
            `;

            preview.appendChild(col);

            col.querySelector(".remove-image")
                .addEventListener("click", () => removeImage(index));

        };

        reader.readAsDataURL(file);

    });

    input.files = dataTransfer.files;
}

function removeImage(index) {

    selectedFiles.splice(index, 1);

    renderImages();
}