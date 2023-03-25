let cardTemplate = `<div class="card w-fit  bg-base-100 shadow-xl img-container"  style="margin-top: 20px">
                        <figure class="figure-padding" >
                            <img src="" class="rounded-xl" alt=""/>
                        </figure>
                        <div id="card-body-height">
                            <div class="card-actions opt-btn-container ">
                                <button class="btn horrible btn-circle btn-ghost" >🥵</button>
                                <button class="btn downvote btn-circle btn-ghost" >😤</button>
                                <button class="btn upvote btn-circle btn-ghost"  >🤣</button>
                            </div>
                        </div>
                    </div>`


let more = `<div id="default" class="card w-fit  bg-base-100 shadow-xl img-container" style="margin-top: 20px">
                <figure class="figure-padding">
                    <img src="https://media-cldnry.s-nbcnews.com/image/upload/t_fit-1120w,f_auto,q_auto:best/rockcms/2022-01/210602-doge-meme-nft-mb-1715-8afb7e.jpg"
                         class="rounded-xl" alt=""/>
                </figure>
                <div id="card-body-height">
                    <div class="card-actions opt-btn-container" style="font-size: larger">
                        🤖 ~没有更多了~ 🤖
                    </div>

                    <a href="/" class="btn  btn-success" style="margin-top: 10px">去投稿</a>

                </div>

            </div>`

const getImgUrl = "/img/content", voteUrl = "/img/vote"

function getToday() {
    let resp = null;
    let uuid = localStorage.getItem("uuid")
    if (uuid === null) {
        uuid = Math.random().toString(36);
        localStorage.setItem("uuid", uuid)
    }

    $.ajax({
        url: `${getImgUrl}?uuid=${uuid}`,
        type: "GET",
        async: false,
        success: function (data) {
            resp = data;
        },
        error: function () {
            resp = [];
        }
    })
    console.log(resp)
    return resp;
}


function vote(name, up = true) {
    let uuid = localStorage.getItem("uuid")
    if (uuid === null) {
        uuid = Math.random().toString(36);
        localStorage.setItem("uuid", uuid)
    }


    $.ajax({
        url: `${voteUrl}?uuid=${uuid}`,
        type: "POST",
        data: {
            name: name,
            up: up
        },
        success: function (data) {
            console.log(data)
        },
        error: function () {
            alert("error")
        }
    })
}

function active(id) {
    let upvote = document.getElementById(id)
    upvote.classList.remove("btn-circle");
    upvote.classList.add("btn-square", "btn-success", "btn-outline");
    upvote.classList.remove("btn-ghost");
}

function deactive(id) {
    let upvote = document.getElementById(id)
    upvote.classList.remove("btn-square", "btn-success", "btn-outline");
    upvote.classList.add("btn-circle", "btn-ghost");
}

function upVote(name) {
    vote(name, true)
    // add btn-square css class remove btn-circle
    let upvote = document.getElementById(`${name}-upvote`)
    let downvote = document.getElementById(`${name}-downvote`)
    let horrible = document.getElementById(`${name}-horrible`)
    active(upvote.id)
    deactive(downvote.id)
    deactive(horrible.id)
}

function downVote(name) {
    vote(name, false)
    let upvote = document.getElementById(`${name}-upvote`)
    let downvote = document.getElementById(`${name}-downvote`)
    let horrible = document.getElementById(`${name}-horrible`)
    active(downvote.id)
    deactive(upvote.id)
    deactive(horrible.id)


}

function horribleVote(name) {
    vote(name, false)
    let upvote = document.getElementById(`${name}-upvote`)
    let downvote = document.getElementById(`${name}-downvote`)
    let horrible = document.getElementById(`${name}-horrible`)
    active(horrible.id)
    deactive(upvote.id)
    deactive(downvote.id)
}

let arr = getToday()


// wait window load
window.onload = function () {
    // id="today"
    let today = document.getElementById("today")
    console.assert(today !== null, "today is null")


    for (let i = 0; i < arr.length; i++) {
        let card = document.createElement("div")
        card.innerHTML = cardTemplate

        let img = card.querySelector("img")
        img.src = arr[i].url

        // set click event
        let upvote = card.querySelector(".upvote")
        upvote.onclick = function () {
            upVote(arr[i].name)
        }
        let downvote = card.querySelector(".downvote")
        downvote.onclick = function () {
            downVote(arr[i].name)
        }

        let horrible = card.querySelector(".horrible")
        horrible.onclick = function () {
            horribleVote(arr[i].name)
        }

        upvote.id = `${arr[i].name}-upvote`
        downvote.id = `${arr[i].name}-downvote`
        horrible.id = `${arr[i].name}-horrible`

        today.appendChild(card)
    }
    // add more
    let moreCard = document.createElement("div")
    moreCard.innerHTML = more
    today.appendChild(moreCard)
}
