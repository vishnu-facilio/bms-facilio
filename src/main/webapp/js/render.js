var page = require("webpage").create();
system = require('system'),

var url = system.args[1];
var output = system.args[2];
var token = system.args[3];
var domainName = system.args[4];

phantom.addCookie({
     name   : 'fc.idToken.facilio',
     value  : token,
     domain : domainName
});

phantom.addCookie({
    name   : 'fc.overrideSession',
    value  : 'true',
    domain : domainName,
    path   : '/'
});

phantom.addCookie({
     name   : 'fc.idToken',
     value  : encodeURIComponent('facilio ' + token),
     domain : domainName,
     path   : '/'
});

phantom.addCookie({
     name   : 'fc.authtype',
     value  : 'facilio',
     domain : domainName,
     path   : '/'
});

page.paperSize = {
    format : 'A4',
    margin: {
        right: '20px',
        left: '20px'
    }
}

page.viewportSize = { width:1024, height:768 };
page.open(url, function (status) {
        if (status !== 'success') {
            console.log('Unable to load the address!');
            phantom.exit(1);
        } else {
            window.setTimeout(function () {
                page.render(output);
                phantom.exit();
            }, 5000);
        }
});