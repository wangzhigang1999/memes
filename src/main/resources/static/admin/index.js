let url = "/img/today"

function getData(token) {
    let uuid = localStorage.getItem("uuid")
    if (uuid === null) {
        uuid = Math.random().toString(36);
        localStorage.setItem("uuid", uuid)
    }
    let resp = null;
    $.ajax({
        url: `${url}?uuid=${uuid}&token=${token}`,
        type: "GET",
        async: false,
        success: function (data) {
            resp = data;
        },
        error: function () {
            resp = [];
        }
    })
    return resp;
}

function deleteImg(name) {
    let token = localStorage.getItem("token");
    let url = "/img/delete"
    let data = {
        "token": token,
        "name": name
    }
    $.ajax({
        url: url,
        type: "POST",
        data: data,
        success: function (data) {
            console.log(data)
            // remove the image from the page
            let img = document.getElementById(name);
            img.remove();
        }
    })


}


function renderBase(token) {
    // get the data from the server
    const data = getData(token);
    // render statistics
    let statistics = document.getElementById("stat");
    statistics.innerHTML = "今日已上传" + data.length + "张图片";
    let div = document.getElementById("frame");
    let html = "";
    for (let i = 0; i < data.length; i++) {
        // get url
        let url = data[i].url;
        let imgName = data[i].name;
        // show image with a delete button below it
        html += "<div id=\"" + imgName + "\" class=\"img-container\">\n" +
            "    <img id='img' src=\"" + url + "\" alt=\"\">\n" +
            "    <button id=\"delete\" class='btn btn-error btn-wide' style='margin-top: 10px;' onclick='deleteImg(" + '"' + imgName + '"' + ")'>删除</button>\n" +
            "</div>"
    }
    div.innerHTML = html;
}

function renderSetToken() {
    let div = document.getElementById("frame");
    div.innerHTML =
        "<div class=\"set-token\" style='text-align: center; vertical-align: middle;'>\n" +
        "    <input type=\"text\" id=\"token\" class='input input-bordered input-accent' placeholder=\"请输入token\">\n" +
        "    <button id=\"submit\" class='btn btn-accent' style='margin-top: 10px'>提交</button>\n" +
        "</div>";
    let submit = document.getElementById("submit");
    submit.onclick = function () {
        let token = document.getElementById("token").value;

        if (token === "") {
            alert("请输入token");
            return;
        }

        // store the token in the local storage
        localStorage.setItem("token", token);
        render()
    }

}

function render() {
    const token = localStorage.getItem("token");
    console.log("token: " + token)
    if (token) {
        renderBase(token)
    } else {
        renderSetToken()
    }
}

render()
