    var currentPosX;
var currentPosY;
var currentImg;
var maxRows=5;

function getX(event) {
    if(!event.pageX) {
    return event.clientX;
    } else {
    return event.pageX - (document.body.scrollLeft || document.documentElement.scrollLeft);
    }
}

function getY(event) {
    if(event.pageY) {
    return event.pageY - (document.body.scrollTop || document.documentElement.scrollTop);
    } else {
    return event.clientY;
    }
}

function fnLoadImage(imgPath, e) {
    currentPosX = getX(e);
    currentPosY = getY(e);

    if (currentImg==null || currentImg!=imgPath) {
    currentImg=imgPath;
    document.getElementById('overlay').innerHTML = "<img src="+imgPath+" onload='fnCallback()' />";
    } else {
    fnCallback();
    }
}

function fnCallback(){
    var overlay = document.getElementById('overlay');
    overlay.style.position = "fixed";
    overlay.style.left = currentPosX+10+'px';
    overlay.style.top = currentPosY+10+'px';
    overlay.style.visibility='visible';
    overlay.style.zIndex=10;
    }

function disable() {
    var overlay = document.getElementById('overlay');
    overlay.style.visibility='hidden';
    }

function discount(amount, price ) {
    if (amount>=5 && amount <10) {
    return price*0.90;
    } else if (amount>=10 && amount <20) {
    return price*0.85;
    } else if (amount>=20 && amount <34) {
    return price*0.80;
    } else if (amount>=35 && amount <49) {
    return price*0.75;
    } else if (amount>=50 && amount <1000) {
    return price*0.70;
    } else if (amount>=1000) {
    return price*0.50;
    } else {
    return price;
    }
}

function discountAmount(amount) {

    if (amount>=5 && amount <10) {
    return '( 5-9 = 10% )';
    } else if (amount>=10 && amount <20) {
        return '( 10-19 = 15% )';
        } else if (amount>=20 && amount <34) {
        return '( 20-34 = 20% )';
        } else if (amount>=35 && amount <49) {
        return '( 35-49 = 25% )';
        } else if (amount>=50 && amount <1000) {
        return '( 50- = 30% )';
        } else if (amount>=1000) {
        return '( jackpot >1000 = 50% )';
        } else {
        return '( 0-4 = 0% )';
        }
    }


    function discountVinyl(price, over100) {
        if (over100>20) {
            return price * 0.75;
        } else if (over100>15) {
            return price * 0.80;
        } else if (over100>10) {
            return price * 0.80;
        } else if (over100>4) {
            return price * 0.85;
        } else if (over100>2) {
            return price*0.10;
        } else {
            return price;
        }
    }

    function vinylDiscountText(over100) {
        if (over100>20) {
            return " ( "+over100+" plader til 75,- eller derover giver 25% rabat) ";
        } else if (over100>15) {
            return " ( "+over100+" plader til 75,- eller derover giver 20% rabat) ";
        } else if (over100>10) {
            return " ( "+over100+" plader til 75,- eller derover giver 20% rabat) ";
        } else if (over100>4) {
            return " ( "+over100+" plader til 75,- eller derover giver 15% rabat) ";
        } else if (over100>2) {
            return " ( "+over100+" plader til 75,- eller derover giver 10% rabat) ";
        } else {
            return " ( "+over100+" plader til 75,- eller derover giver ingen rabat) ";
        }
    }

    function collectRecords(isVinyl) {

        var html='';
        var newColor = "eaeaf1";
        var totalprice = 0;
        var bought = 0;
        var over100 = 0;

        for (i=1;i<=maxRows;i++) {
            var chid = 'c'+i;
            var recordid = 'r'+i;
            var priceid = 'price'+i;
            var ch = document.getElementById(chid);
            if (ch!=null && ch.checked) {
                var recordRow = document.getElementById(recordid);
                var recordPrice = document.getElementById(priceid);
                var price = recordPrice.innerHTML;
                price = price.substring(0, price.length-2);
                if (price>=75) {
                    over100++;
                }
                totalprice += parseInt(price);
                var recordHtml = recordRow.outerHTML;
                recordHtml = recordHtml.replace('ffffff',newColor);
                recordHtml = recordHtml.replace('c7e1cf',newColor);

                html+=recordHtml;
                bought++;
            }
        }

        var priceHtml = '';
        if (isVinyl==true) {
            priceHtml="<br/><b>Total pris for vinyler: </b>"+totalprice+",-  <b>med prisrabat: "+vinylDiscountText(over100)+"</b>"+ Math.round(discountVinyl(totalprice, over100))+",- <br/>";
        } else {
            priceHtml="<br/><b>Total pris for cder: </b>"+totalprice+",-  <b>med samlerabat "+discountAmount(bought)+" : </b>"+ Math.round(discount(bought, totalprice))+",- <br/>";
        }


        var result = '<html><head><title>Records</title><head><body><br/><table>'+html+'</table><br/>'+priceHtml+'</body></html>'

        var win = window.open("", "Records", "scrollbars=yes, resizable=yes");
        win.document.body.innerHTML = result;

    }
