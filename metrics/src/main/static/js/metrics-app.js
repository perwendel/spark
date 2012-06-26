window.addEvent('domready', function ()
{
    new Metrics(document.id('occupation-service'), {
        url:'http://prod-occupation-service-1522070611.eu-west-1.elb.amazonaws.com/metrics',
        requestDelay:5000,
        shiftAfter:50
    });

    new Metrics(document.id('enquiry-search-service'), {
        url:'http://prod-enquiry-search-service-1387825206.eu-west-1.elb.amazonaws.com/metrics'
    });
});