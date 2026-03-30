document.addEventListener('DOMContentLoaded', function () {
        var deleteModal = document.getElementById('deleteModal');
        if (deleteModal) {
            deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var customerId = button.getAttribute('data-id');
            var customerName = button.getAttribute('data-name');
            var modalBodyInput = deleteModal.querySelector('.modal-body #customerName');
            var deleteForm = deleteModal.querySelector('#deleteForm');

            modalBodyInput.textContent = customerName;
            deleteForm.action = '/customers/delete/' + customerId;
        });
    }
});