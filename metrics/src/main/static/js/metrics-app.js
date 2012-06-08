window.addEvent('domready', function() {
	new Metrics(document.id('occupation-service'), {
		url: 'http://localhost:4600/metrics',
		requestDelay: 5000,
		shiftAfter: 50
	});
	
	new Metrics(document.id('enquiry-search-service'), {
		url: 'http://localhost:4610/metrics'
	});
});