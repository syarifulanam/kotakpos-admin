document.addEventListener('DOMContentLoaded', function () {
    var deleteItemModal = document.getElementById('deleteItemModal');
    if (deleteItemModal) {
        deleteItemModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var deleteUrl = button.getAttribute('data-delete-url');
            var itemName = button.getAttribute('data-item-name');

            var modalBody = deleteItemModal.querySelector('.modal-body #itemName');
            var deleteForm = deleteItemModal.querySelector('#deleteItemForm');

            modalBody.textContent = itemName;
            deleteForm.action = deleteUrl;
        });
    }
});