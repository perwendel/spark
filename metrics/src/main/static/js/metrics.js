/*
 ---

 name: Metrics

 description: provides an example interface for spark and HighCharts

 authors: Dimitar Christoff, QMetric Group Ltd.

 license: MIT-style license.

 version: 1.08

 requires:
 - Core/String
 - Core/Event
 - Core/Element
 - Core/Array
 - Core/Class
 - Core/Request

 provides: Metrics

 ...
 */
;(function() {

	var Metrics = this.Metrics = new Class({
	
		Implements: [Options, Events],
	
		options: {
			wrapper: 'div.metrics',
			urlRoot: 'proxy/?url=',
			url: '',
			requestDelay: 3000,
			shiftAfter: 30
		},
	
		initialize: function(el, options) {
			this.setOptions(options);
			this.element = document.id(el) || new Element(this.options.canvas);
			this.setUp();
		},
	
		setUp: function() {
			var self = this;
			this.lines = {};
			this.request = new Request.JSON({
				url: this.options.urlRoot + encodeURIComponent(this.options.url),
				method: 'get',
				link: 'cancel',
				onSuccess: this.newData.bind(this),
				onFailure: function() {
					self.fireEvent('error');
				}
			});
	
			this.counter = 0;
			this.request.send.periodical(this.options.requestDelay, this.request);
		},
	
		newData: function(data) {
			var self = this;
			this.counter++;
			Object.each(data, function(value, key) {
				var after = function() {
					var shift = !!(self.counter >= self.options.shiftAfter),
						date = (new Date()).getTime();
	
					self.lines[key].chart.series[0].addPoint([date, value.timer.duration.mean], true, shift);
					self.lines[key].chart.series[1].addPoint([date, value.timer.rate.mean], true, shift);
				};
	
				if (!self.lines[key]) {
					self.addMonitor(key, value, after);
				}
				else {
					after.call(self);
				}
	
			});
			// this.counter >= 30 && (this.counter = 0);
		},
	
		addMonitor: function(label, value, after) {
			var l,
				o = this.options;
	
	
			this.lines[label] = {
				target: new Element(o.wrapper)
			};
	
			l = this.lines[label];
	
			l.chart = new Highcharts.Chart({
				chart: {
					renderTo: l.target,
					type: 'area',
					marginRight: 10
				},
				title: {
					text: label
				},
				xAxis: {
					type: 'datetime',
					tickPixelInterval: 150
				},
				yAxis: {
					title: {
						text: 'Value'
					},
					plotLines: [{
						value: 0,
						width: 1,
						color: '#808080'
					}]
				},
				tooltip: {
					formatter: function() {
						return '<b>'+ this.series.name +'</b><br/>'+
							Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
							Highcharts.numberFormat(this.y, 2);
					}
				},
				legend: {
					enabled: true
				},
				exporting: {
					enabled: false
				},
				credits: {
					enabled: false
				},
				series: [{
					name: 'Duration (ms)',
					data: [{x: (new Date()).getTime(), y: value.timer.duration.mean}]
				}, {
					name: 'Rate (s)',
					data: [{x: (new Date()).getTime(), y: value.timer.rate.mean}]
				}]
			});
	
			l.target.inject(this.element);
			after && after.call(this);
		}
	});
}());