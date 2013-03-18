function customize() {
	if(document.URL.toLowerCase().indexOf("www.angeviline.org") != -1) {
    	var p = document.getElementById("titlelink");
    	if(p)
    		p.firstChild.nodeValue = "ANGEL EVILINE";
    	p = document.getElementById("play");
    	if(p)
    		p.firstChild.nodeValue = "PLAY ANGEL EVILINE";
    	document.title = "ANGEL EVILINE";
    }
	if(document.URL.toLowerCase().indexOf("www.myndzeviline.org") != -1) {
    	var p = document.getElementById("titlelink");
    	if(p)
    		p.firstChild.nodeValue = "myndzi EVILINE";
    	p = document.getElementById("play");
    	if(p)
    		p.firstChild.nodeValue = "PLAY myndzi EVILINE";
    	document.title = "myndzi EVILINE";
    }
}           

function play() {
    var attributes = { code:'org.eviline.runner.MainApplet',  width:'100%', height:'100%'} ;
    var parameters = {jnlp_href: 'eviline.jnlp', score_host:'www.eviline.org:8080'} ;
    
    if(document.URL.toLowerCase().indexOf("www.angeviline.org") != -1) {
    	parameters = {jnlp_href: 'eviline.jnlp', score_host:'www.angeviline.org:8080', angelic:'true'} ;
    	document.body.innerHTML = document.body.innerHTML.replace("EVILINE", "ANGEL EVILINE")
    }
    if(document.URL.toLowerCase().indexOf("www.myndzeviline.org") != -1) {
    	parameters = {jnlp_href: 'eviline.jnlp', score_host:'www.myndzeviline.org:8080', myndzi:'true'} ;
    	document.body.innerHTML = document.body.innerHTML.replace("EVILINE", "myndzi EVILINE")
    }
    	
    deployJava.runApplet(attributes, parameters, '1.6');
}