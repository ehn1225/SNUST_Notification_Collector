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