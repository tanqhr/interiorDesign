document.addEventListener("DOMContentLoaded", function () {

    loadFaqs();


    // ==========================================
    // Зареждане на всички FAQ
    // ==========================================

    function loadFaqs() {

        fetch("/api/faqs/all")
            .then(response => {

                if (!response.ok) {
                    throw new Error("Грешка при зареждане на FAQ");
                }

                return response.json();
            })
            .then(faqs => {

                renderFaqs(faqs);

            })
            .catch(error => {

                console.error(error);

                showMessage(
                    "Грешка при зареждане на FAQ.",
                    "danger"
                );
            });
    }


    // ==========================================
    // Показване на FAQ в таблицата
    // ==========================================

    function renderFaqs(faqs) {

        const tableBody =
            document.getElementById("faqTableBody");

        tableBody.innerHTML = "";

        if (faqs.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5"
                        class="text-center text-muted py-4">
                        Няма добавени FAQ.
                    </td>
                </tr>
            `;

            return;
        }


        faqs.forEach(faq => {

            const row =
                document.createElement("tr");

            row.innerHTML = `

                <td>
                    ${faq.id}
                </td>

                <td>
                    ${escapeHtml(faq.question)}
                </td>

                <td>
                    ${faq.category
                ? escapeHtml(faq.category)
                : "-"}
                </td>

                <td>

                    ${faq.active
                ? `<span class="badge bg-success">
                                Активен
                           </span>`
                : `<span class="badge bg-secondary">
                                Неактивен
                           </span>`
            }

                </td>

                <td class="text-end">

                    <button
                        class="btn btn-sm btn-outline-primary me-1"
                        onclick="editFaq(${faq.id})">

                        Редактирай

                    </button>


                    <button
                        class="btn btn-sm ${faq.active
                ? "btn-outline-warning"
                : "btn-outline-success"} me-1"
                        onclick="toggleFaq(${faq.id})">

                        ${faq.active
                ? "Деактивирай"
                : "Активирай"}

                    </button>


                    <button
                        class="btn btn-sm btn-outline-danger"
                        onclick="deleteFaq(${faq.id})">

                        Изтрий

                    </button>

                </td>
            `;

            tableBody.appendChild(row);

        });
    }


    // ==========================================
    // Отваряне на прозореца за добавяне
    // ==========================================

    window.openAddModal = function () {

        document.getElementById("faqModalTitle")
            .textContent = "Добавяне на FAQ";

        document.getElementById("faqId")
            .value = "";

        document.getElementById("question")
            .value = "";

        document.getElementById("answer")
            .value = "";

        document.getElementById("category")
            .value = "";

        document.getElementById("active")
            .checked = true;


        const modal =
            new bootstrap.Modal(
                document.getElementById("faqModal")
            );

        modal.show();
    };


    // ==========================================
    // Редактиране
    // ==========================================

    window.editFaq = function (id) {

        fetch(`/api/faqs/all`)
            .then(response => response.json())
            .then(faqs => {

                const faq =
                    faqs.find(item => item.id === id);

                if (!faq) {
                    throw new Error("FAQ не е намерен.");
                }


                document.getElementById("faqModalTitle")
                    .textContent = "Редактиране на FAQ";

                document.getElementById("faqId")
                    .value = faq.id;

                document.getElementById("question")
                    .value = faq.question;

                document.getElementById("answer")
                    .value = faq.answer;

                document.getElementById("category")
                    .value = faq.category || "";

                document.getElementById("active")
                    .checked = faq.active;


                const modal =
                    new bootstrap.Modal(
                        document.getElementById("faqModal")
                    );

                modal.show();

            })
            .catch(error => {

                console.error(error);

                showMessage(
                    "Грешка при зареждане на FAQ.",
                    "danger"
                );

            });
    };


    // ==========================================
    // Добавяне / редактиране
    // ==========================================

    window.saveFaq = function () {

        const id =
            document.getElementById("faqId").value;

        const question =
            document.getElementById("question").value.trim();

        const answer =
            document.getElementById("answer").value.trim();

        const category =
            document.getElementById("category").value.trim();

        const active =
            document.getElementById("active").checked;


        if (!question || !answer) {

            showMessage(
                "Въпросът и отговорът са задължителни.",
                "warning"
            );

            return;
        }


        const faq = {

            question: question,

            answer: answer,

            category: category,

            active: active
        };


        let url = "/api/faqs";

        let method = "POST";


        // Ако има ID → редактиране
        if (id) {

            url = `/api/faqs/${id}`;

            method = "PUT";
        }


        fetch(url, {

            method: method,

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(faq)

        })
            .then(response => {

                if (!response.ok) {
                    throw new Error("Грешка при запазване.");
                }

                return response.json();

            })
            .then(() => {

                closeModal();

                showMessage(
                    id
                        ? "FAQ беше редактиран успешно."
                        : "FAQ беше добавен успешно.",
                    "success"
                );

                loadFaqs();

            })
            .catch(error => {

                console.error(error);

                showMessage(
                    "Възникна грешка при запазване.",
                    "danger"
                );

            });
    };


    // ==========================================
    // Активиране / деактивиране
    // ==========================================

    window.toggleFaq = function (id) {

        fetch(`/api/faqs/${id}/active`, {

            method: "PATCH"

        })
            .then(response => {

                if (!response.ok) {
                    throw new Error(
                        "Грешка при промяна на статуса."
                    );
                }

                return response.json();

            })
            .then(() => {

                showMessage(
                    "Статусът на FAQ беше променен.",
                    "success"
                );

                loadFaqs();

            })
            .catch(error => {

                console.error(error);

                showMessage(
                    "Неуспешна промяна на статуса.",
                    "danger"
                );

            });
    };


    // ==========================================
    // Изтриване
    // ==========================================

    window.deleteFaq = function (id) {

        if (!confirm(
            "Сигурни ли сте, че искате да изтриете този FAQ?"
        )) {
            return;
        }


        fetch(`/api/faqs/${id}`, {

            method: "DELETE"

        })
            .then(response => {

                if (!response.ok) {
                    throw new Error(
                        "Грешка при изтриване."
                    );
                }

            })
            .then(() => {

                showMessage(
                    "FAQ беше изтрит успешно.",
                    "success"
                );

                loadFaqs();

            })
            .catch(error => {

                console.error(error);

                showMessage(
                    "Неуспешно изтриване на FAQ.",
                    "danger"
                );

            });
    };


    // ==========================================
    // Затваряне на Modal
    // ==========================================

    function closeModal() {

        const modalElement =
            document.getElementById("faqModal");

        const modal =
            bootstrap.Modal.getInstance(modalElement);

        if (modal) {
            modal.hide();
        }
    }


    // ==========================================
    // Съобщения
    // ==========================================

    function showMessage(message, type) {

        const messageElement =
            document.getElementById("message");

        messageElement.textContent = message;

        messageElement.className =
            `alert alert-${type}`;

        setTimeout(() => {

            messageElement.classList.add("d-none");

        }, 3000);
    }


    // ==========================================
    // Защита при показване на текст
    // ==========================================

    function escapeHtml(text) {

        const div =
            document.createElement("div");

        div.textContent = text;

        return div.innerHTML;
    }

});