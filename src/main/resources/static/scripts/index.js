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
                createQuestionForDisplay(xhr.responseText);
            }
        } else if (xhr.status !== 200) {
            console.log('Request failed.  Returned status of ' + xhr.status);
            //showEditError();
        }
    };
    xhr.send(formData);
}

function createQuestionForDisplay(textReturned) {
    const question = JSON.parse(textReturned);
    let questionBody = document.getElementById("questionBody");
    questionBody.appendChild(createButtonsForQuestionNumber(question.number));
    questionBody.appendChild(createTextForQuestion(question.question));
}

function createButtonsForQuestionNumber(number) {
    let divNumber = document.createElement("div");
    divNumber.setAttribute("class", "col-md-12");
    let previousButton = aButton("qNumber", "<<");
    let txtNum = aTextBox("qNumber", number);
    let nextButton = aButton("qNumber", ">>");
    divNumber.appendChild(previousButton);
    divNumber.appendChild(txtNum);
    divNumber.appendChild(nextButton);
    return divNumber;
}

function createTextForQuestion(q) {
    let divQText = document.createElement("div");
    divQText.setAttribute("class", "col-md-12");
    let txtQ = aTextBox("question", q);
    divQText.appendChild(txtQ);
    return divQText;
}

function aTextBox(id, value) {
    let txtBox = document.createElement("input");
    txtBox.setAttribute("type", "text");
    txtBox.setAttribute("class", "form-control");
    txtBox.setAttribute("value", value);
    txtBox.setAttribute("id", id);
    return txtBox;
}

function aButton(id, text) {
    let txtBox = document.createElement("button");
    txtBox.setAttribute("type", "button");
    txtBox.setAttribute("class", "btn btn-primary");
    txtBox.setAttribute("text", text);
    txtBox.setAttribute("id", id);
    return txtBox;
}