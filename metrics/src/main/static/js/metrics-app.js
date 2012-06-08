var Metrics = new Class({

    Implements: [Options, Events],

    options: {
        canvas: 'canvas.metrics',
        canvasWrapper: 'div.canvas',
        label: 'div.label',
        urlRoot: 'proxy/?url=',
        url: 'http://localhost:4600/metrics',
        chartOptions: {
            grid: {
                strokeStyle: 'rgb(125, 0, 0)',
                fillStyle: 'rgb(60, 0, 0)',
                lineWidth: 1,
                millisPerLine: 250,
                verticalSections: 6
            },
            labels: {
                fillStyle: 'rgb(255,250,205)'
            }
        },
        chartDelay: 5000,
        requestDelay: 5000,
        timeLines: 2,
        lineStyles: [{
            strokeStyle: 'rgb(0, 255, 0)',
            fillStyle: 'rgba(0, 255, 0, 0.4)',
            lineWidth: 3
        }, {
            strokeStyle: 'rgb(255, 0, 255)',
            fillStyle: 'rgba(255, 0, 255, 0.3)',
            lineWidth: 3
        }]
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

        this.request.send.periodical(this.options.requestDelay, this.request);
    },

    newData: function(data) {
        var self = this;
        Object.each(data, function(value, key) {
            self.lines[key] || self.addMonitor(key, value);
            // console.info(value.timer.duration.mean);
            self.lines[key].timeline[0].append(new Date().getTime(), value.timer.duration.mean);
            self.lines[key].timeline[1].append(new Date().getTime(), value.timer.rate.mean * 10000);
        });
    },

    addMonitor: function(label, value) {
        var l,
            o = this.options,
            html = label,
            index = 0,
            dot = '.';

        if (Metrics.map[label]) {
            html = [Metrics.map[label].label];
            Object.each(Metrics.map[label].monitors, function(value, key) {
                html.push('<div style="color:'+o.lineStyles[index].strokeStyle+'">'+value+'</div>');
                index++;
            });

            html = html.join('');
        }



        this.lines[label] = {
            canvas: new Element(o.canvasWrapper).adopt([new Element(o.canvas), new Element(o.label, {
                html: html
            })]),
            // MDN > "callback is invoked only for indexes of the array which have assigned values"
            timeline: Array(o.timeLines).join(dot).split(dot).map(function() { return new TimeSeries(); }),
            chart: new SmoothieChart(o.chartOptions)
        };


        l = this.lines[label];

        Array.each(l.timeline, function(line, index) {
            l.chart.addTimeSeries(line, o.lineStyles[index]);
        });

        l.chart.streamTo(l.canvas.getFirst(), this.options.chartDelay);
        l.canvas.inject(this.element);


    }


});


Metrics.map = {
    'com.qmetric.occupation.server.handlers.BeginsWithOccupationHandler': {
        label: 'Occupation - Begins With',
        monitors: {
            duration: 'Response time (ms)',
            rate: 'Throughput (sec)'
        }
    },
    'com.qmetric.occupation.server.handlers.ExactOccupationHandler': {
        label: 'Occupation - Exact',
        monitors: {
            duration: 'Response time (ms)',
            rate: 'Throughput (sec)'
        }
    }
};



var m = new Metrics(document.id('metrics'));
