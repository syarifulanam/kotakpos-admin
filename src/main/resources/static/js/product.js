document.addEventListener('DOMContentLoaded', function () {
    JsBarcode(".barcode").init();

    var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var productId = button.getAttribute('data-id');
            var productName = button.getAttribute('data-name');
            var modalBodyInput = deleteModal.querySelector('.modal-body #productName');
            var deleteForm = deleteModal.querySelector('#deleteForm');

            modalBodyInput.textContent = productName;
            deleteForm.action = '/products/delete/' + productId;
        });
    }
});