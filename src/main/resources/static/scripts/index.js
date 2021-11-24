
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
    questionBody.appendChild(createTextArea(question.question));
    questionBody.appendChild(createOptions(question.options));
    questionBody.appendChild(createTextArea(question.explanation));
    questionBody.appendChild(createText(question.reference));
    questionBody.appendChild(createText(question.tags));
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

function createOptions(opt) {
    let divOpt = document.createElement("div");
    divOpt.setAttribute("class", "col-md-12");
    console.log('total # of options are :'+ opt.length);
    const isOption = isSingleSelect(opt[0]);
    for (let i = 0; i < opt.length; i++) {
        console.log(opt[i].text);
        const optId = 'o' + i;
        let isAnswer = isAnswerSelected(opt[i].isAnswer);
        console.log(isAnswer);
        if (isOption) {
            const option = aRadioInDiv(optId, opt[i].text, isAnswer);
            divOpt.appendChild(option);
        } else {
            const option = aCheckBoxInDiv(optId, opt[i].text, isAnswer);
            divOpt.appendChild(option);
        }
    }
    return divOpt;
}

function isAnswerSelected(answerString) {
    return answerString === "true";
}

function isSingleSelect(firstOption) {
    return firstOption.type === "radio";
}

function aCheckBox(id, isChecked) {
    let check = document.createElement("input");
    check.setAttribute("type", "check");
    check.setAttribute("class", "form-check-input");
    check.setAttribute("id", id);
    if (isChecked) {
        check.checked = true;
    }
    return check;
}

function aRadioBox(id, isChecked) {
    let radio = document.createElement("input");
    radio.setAttribute("type", "radio");
    radio.setAttribute("class", "form-check-input");
    radio.setAttribute("id", id);
    if (isChecked) {
        radio.checked = true;
    }
    return radio;
}

function aRadioInDiv(id, value, isChecked) {
    let divRadio = document.createElement("div");
    divRadio.setAttribute("class", "form-check");
    const option = aRadioBox(id, isChecked);
    const radioLabel = labelForRadio(id, value);
    divRadio.appendChild(option);
    divRadio.appendChild(radioLabel);
    return divRadio;
}

function aCheckBoxInDiv(id, value, isChecked) {
    let divRadio = document.createElement("div");
    divRadio.setAttribute("class", "form-check");
    const option = aCheckBox(id, isChecked);
    const radioLabel = labelForRadio(id, value);
    divRadio.appendChild(option);
    divRadio.appendChild(radioLabel);
    return divRadio;
}

function labelForRadio(forField, text) {
    let label = document.createElement("label");
    label.setAttribute("class", "form-check-label");
    label.innerHTML = text;
    label.setAttribute("for", forField);
    return label;
}

function createText(q) {
    let divQText = document.createElement("div");
    divQText.setAttribute("class", "col-md-12");
    let txtQ = aTextBox("id", q);
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

function createTextArea(q) {
    let divQText = document.createElement("div");
    divQText.setAttribute("class", "col-md-12");
    let txtQ = aTextArea("id", q);
    divQText.appendChild(txtQ);
    return divQText;
}

function aTextArea(id, value) {
    let txtArea = document.createElement("textarea");
    txtArea.setAttribute("rows", "3");
    txtArea.setAttribute("class", "form-control");
    txtArea.setAttribute("id", id);
    txtArea.value = value;
    return txtArea;
}

function aButton(id, text) {
    let txtBox = document.createElement("button");
    txtBox.setAttribute("type", "button");
    txtBox.setAttribute("class", "btn btn-primary");
    txtBox.setAttribute("text", text);
    txtBox.setAttribute("id", id);
    return txtBox;
}