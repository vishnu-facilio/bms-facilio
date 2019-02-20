var page = require("webpage").create();
system = require('system'),
phantom.addCookie({
     name   : 'fc.idToken.facilio',
     value  : system.args[3],
     domain : system.args[4],
     path   : '/'
});

phantom.addCookie({
    name   : 'fc.overrideSession',
    value  : 'true',
    domain : system.args[4],
    path   : '/'
});

phantom.addCookie({
     name   : 'fc.idToken',
     value  : 'facilio ' + system.args[3],
     domain : system.args[4],
     path   : '/'
});

phantom.addCookie({
     name   : 'fc.authtype',
     value  : 'facilio',
     domain : system.args[4],
     path   : '/'
});

page.paperSize = {
    format : 'A4',
    margin: {
        right: '20px',
        left: '20px'
    }
}

var pageurl=system.args[1];
var output=system.args[2];

page.viewportSize = { width:1024, height:768 };
page.open(pageurl, function (status) {
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
