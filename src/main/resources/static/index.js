function compressImg(file, quality) {
    if (file[0]) {
        return Promise.all(Array.from(file).map(e => compressImg(e, quality))) // 如果是 file 数组返回 Promise 数组
    } else {
        return new Promise((resolve) => {
            const reader = new FileReader() // 创建 FileReader
            reader.onload = ({target: {result: src}}) => {
                const image = new Image() // 创建 img 元素
                image.onload = async () => {
                    const canvas = document.createElement('canvas') // 创建 canvas 元素
                    canvas.width = image.width
                    canvas.height = image.height
                    canvas.getContext('2d').drawImage(image, 0, 0, image.width, image.height) // 绘制 canvas
                    const canvasURL = canvas.toDataURL('image/jpeg', quality)
                    const buffer = atob(canvasURL.split(',')[1])
                    let length = buffer.length
                    const bufferArray = new Uint8Array(new ArrayBuffer(length))
                    while (length--) {
                        bufferArray[length] = buffer.charCodeAt(length)
                    }
                    const miniFile = new File([bufferArray], file.name, {type: 'image/jpeg'})

                    globalFile = miniFile

                    console.log('压缩前', file.size / 1024, 'KB')
                    console.log('压缩后', miniFile.size / 1024, 'KB')
                    console.log('压缩率', (miniFile.size / file.size * 100).toFixed(2) + '%')

                    resolve({
                        file: miniFile,
                        origin: file,
                        beforeSrc: src,
                        afterSrc: canvasURL,
                        beforeKB: Number((file.size / 1024).toFixed(2)),
                        afterKB: Number((miniFile.size / 1024).toFixed(2))
                    })
                }
                image.src = src
            }
            reader.readAsDataURL(file)
        })
    }
}


// check img type
function findImgExt(file) {
    let ext = file.name.split('.').pop();
    if (ext === file.name) {
        ext = '';
    }
    return ext;
}


function checkFileSize(file) {
    return file.size <= 1024 * maxFileSizeKB;
}


function upload() {
    const sub = document.getElementById("sub").checked;
    const title = document.getElementById("title");
    const resp = document.getElementById("resp");
    if (globalFile == null) {
        title.innerHTML = "别急！";
        resp.innerHTML = "先选择文件";
        return
    }
    let fileType = findImgExt(globalFile)
    if (fileType == null || fileType === "gif") {
        title.innerHTML = "出错啦！";
        resp.innerHTML = "不支持的文件格式！";
        return
    }
    if (!checkFileSize(globalFile)) {
        title.innerHTML = "出错啦！";
        resp.innerHTML = "文件过大,最大支持" + maxFileSizeKB + "KB";
        return
    }

    title.innerHTML = "上传中...";
    resp.innerHTML = "请稍后";

    const formData = new FormData();
    formData.append("file", globalFile);
    formData.append("personal", !sub);
    $.ajax({
        url: "img/upload",
        // url: "http://localhost:8080/img/upload",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            const title = document.getElementById("title");
            const resp = document.getElementById("resp");
            const left = "[md]\n" + "\n" + "![图片链接](";
            const right = ")\n" + "\n" + "[/md]";
            const url = left + data.url + right;
            try {
                navigator.clipboard.writeText(url).then(() =>
                    console.log('复制成功')
                );
                title.innerHTML = "上传成功,Markdown 链接已经复制到剪切板！";
            } catch (e) {
                title.innerHTML = "上传成功,复制下边的链接去发帖！";
                console.log(e)
            } finally {
                resp.innerHTML = url;

            }
        },
        error: function (data) {
            const title = document.getElementById("title");
            const resp = document.getElementById("resp");
            title.innerHTML = "上传失败";
            resp.innerHTML = "稍后重试一下，或者去 " + "<a class='btn btn-xs btn-success' onclick='navigateTo(1)' >Github</a>" + " 提issue."
        }
    });
}