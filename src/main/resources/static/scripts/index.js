function getQuestion() {
    // call ajax to get the question
    let questionFile = document.getElementById('questionFile');
    console.log(questionFile.value);

    let xhr = new XMLHttpRequest();
    let formData = new FormData();
    let files = questionFile.files;
    let file = files[0];

    formData.append('upload', file, file.name);

    xhr.open('POST', "/question/uploadForEdit", true);
    let token = document.querySelector("meta[name='_csrf']").content;
    //let header = document.querySelector("meta[name='_csrf_header']").content;
    const header = "X-CSRF-TOKEN";
    xhr.setRequestHeader(header, token);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
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