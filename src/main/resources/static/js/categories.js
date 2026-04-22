document.addEventListener('DOMContentLoaded', function () {
    var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var categoryId = button.getAttribute('data-id');
            var categoryName = button.getAttribute('data-name');
            var modalBodyInput = deleteModal.querySelector('.modal-body #categoryName');
            var deleteForm = deleteModal.querySelector('#deleteForm');

            modalBodyInput.textContent = categoryName;
            deleteForm.action = '/categories/delete/' + categoryId;
        });
    }
});