function customize() {
}           

function play() {
    var attributes = { code:'org.eviline.runner.MainApplet',  width:'750', height:'750'} ;
    var parameters = {jnlp_href: 'http://www.eviline.org:8080/eviline-tomcat/eviline.jnlp', score_host:'www.eviline.org:8080'} ;
    
    deployJava.runApplet(attributes, parameters, '1.6');
}