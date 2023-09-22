jQuery(document).ready(function($) {
    $("#menu_nav").hide();
    $("#menu").click(function() {
        if($("#menu_nav").is(":hidden")) {
            document.getElementById("menu").style.backgroundPosition = "0 -16px";
            document.getElementById("menu").style.transform ="rotate(-180deg)";
        }
        else{
            document.getElementById("menu").style.backgroundPosition = "0 0";
            document.getElementById("menu").style.transform ="rotate(0deg)";
        }
        $("#menu_nav").slideToggle(300);
    });
        
    $('a').click(function(){
        $('html, body').animate({
            scrollTop: $( $.attr(this, 'href') ).offset().top
        }, 500);
        if($("#menu_nav").is(":visible")) {
            document.getElementById("menu").style.backgroundPosition = "0 0";
            document.getElementById("menu").style.transform ="rotate(0deg)";
            $("#menu_nav").slideToggle(300);
        }
        return false;
    });
});

$(function () { 
    tab('#tab',0);  
});

function tab(e, num){
    var num = num || 0;
    var menu = $(e).children();
    var con = $(e+'_con').children();
    var select = $(menu).eq(num);
    var i = num;

    select.addClass('active');
    con.eq(num).show();

    menu.click(function(){
        if(select!==null){
            select.removeClass("active");
            con.eq(i).hide();
        }

        select = $(this);   
        i = $(this).index();

        select.addClass('active');
        con.eq(i).show();
    });
}

function DateFormatServer(date) {
    //date format : yyyymmdd
    var year = date.getFullYear().toString();
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var day = date.getDate().toString().padStart(2, '0');
    return year + month + day;
}
function FormatDateToYYYYMMDD(date) {
    //date format : yyyy년 mm월 dd일
    var year = date.getFullYear();
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var day = date.getDate().toString().padStart(2, '0');
    return year + '년 ' + month + '월 ' + day + '일';
}

function ChangeDate(value){
    today.setDate(today.getDate() + value);
    UpdateNotice(today);
}

function UpdateNotice(requestDate){
    var serverDate = DateFormatServer(requestDate);
    $.getJSON('/getData?date=' + serverDate, function (data) {
        var list = document.getElementById('itemList');
        list.innerHTML = "";
        if(data.length == 0){
            list.innerHTML = "<h3 align=center>공지사항이 없습니다.</h3>";
        }
        data.forEach(item => {
            var item = `
                <div class="profile">
                    <h3 class="profile-name">${item.category}</h3>
                    <p class="profile-description">
                        <a href="${item.url}" target="_blank">${item.title}</a><br>
                        <small>${item.uploader}</small>
                    </p>
                </div>
            `;
            list.innerHTML += item;
        });
    });

    var dateBox = document.getElementById('dateBox');
    dateBox.innerText = FormatDateToYYYYMMDD(requestDate);
}