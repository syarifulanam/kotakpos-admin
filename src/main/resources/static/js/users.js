document.addEventListener('DOMContentLoaded', function () {
var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var userId = button.getAttribute('data-id');
            var userName = button.getAttribute('data-name');
            var modalBodyInput = deleteModal.querySelector('.modal-body #userName');
            var deleteForm = deleteModal.querySelectore('#deleteForm');

            modalBodyInput.textContent = userName;
            deleteForm.action = '/users/delete/' + userId;
        });
    }
});