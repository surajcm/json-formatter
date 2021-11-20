function getQuestion() {
    // call ajax to get the question
    let questionFile = document.getElementById('questionFile');
    console.log(questionFile.value);

    let xhr = new XMLHttpRequest();
    let formData = new FormData();
    let files = questionFile.files;
    let file = files[0];

    formData.append('file', file, file.name);
    xhr.open('POST', "/api/upload", true);
    let token = document.querySelector("meta[name='_csrf']").content;
    const header = "X-CSRF-TOKEN";
    xhr.setRequestHeader(header, token);
    xhr.onload = function () {
        if (xhr.status === 200) {
            if (xhr.responseText != null) {
                console.log(xhr.responseText);
                //populateDataForEdit(xhr.responseText);
            }
        } else if (xhr.status !== 200) {
            console.log('Request failed.  Returned status of ' + xhr.status);
            //showEditError();
        }
    };
    xhr.send(formData);
}