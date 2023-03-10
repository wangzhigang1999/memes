function getData(token) {
    let url = "/img/today?token=" + token;
    let resp = null;
    $.ajax({
        url: url,
        type: "GET",
        async: false,
        success: function (data) {
            resp = data;
        },
        error: function () {
            resp = ["https://oss-bbs.bupt.site/example.jpg"];
        }
    })
    return resp;
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
        let url = String(data[i]).split("](")[1].split(")")[0];
        // show image with a delete button below it
        html += "<div id=\"" + i + "\" class=\"img-container\">\n" +
            "    <img id='img' src=\"" + url + "\" alt=\"\">\n" +
            "</div>"
    }
    div.innerHTML = html;
}

function renderSetToken() {
    let div = document.getElementById("frame");
    div.innerHTML =
        "<div class=\"set-token\">\n" +
        "    <input type=\"text\" id=\"token\" class='input input-bordered input-accent' placeholder=\"请输入token\">\n" +
        "    <button id=\"submit\" class='btn btn-accent'>提交</button>\n" +
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
