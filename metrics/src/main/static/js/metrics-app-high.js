Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

Highcharts.theme = {
    colors: ["#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
        "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
    chart: {
        backgroundColor: {
            linearGradient: [0, 0, 0, 400],
            stops: [
                [0, 'rgb(96, 96, 96)'],
                [1, 'rgb(16, 16, 16)']
            ]
        },
        borderWidth: 0,
        borderRadius: 15,
        plotBackgroundColor: null,
        plotShadow: false,
        plotBorderWidth: 0
    },
    title: {
        style: {
            color: '#FFF',
            font: '16px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
        }
    },
    subtitle: {
        style: {
            color: '#DDD',
            font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
        }
    },
    xAxis: {
        gridLineWidth: 0,
        lineColor: '#999',
        tickColor: '#999',
        labels: {
            style: {
                color: '#999',
                fontWeight: 'bold'
            }
        },
        title: {
            style: {
                color: '#AAA',
                font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
            }
        }
    },
    yAxis: {
        alternateGridColor: null,
        minorTickInterval: null,
        gridLineColor: 'rgba(255, 255, 255, .1)',
        lineWidth: 0,
        tickWidth: 0,
        labels: {
            style: {
                color: '#999',
                fontWeight: 'bold'
            }
        },
        title: {
            style: {
                color: '#AAA',
                font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
            }
        }
    },
    legend: {
        itemStyle: {
            color: '#CCC'
        },
        itemHoverStyle: {
            color: '#FFF'
        },
        itemHiddenStyle: {
            color: '#333'
        }
    },
    labels: {
        style: {
            color: '#CCC'
        }
    },
    tooltip: {
        backgroundColor: {
            linearGradient: [0, 0, 0, 50],
            stops: [
                [0, 'rgba(96, 96, 96, .8)'],
                [1, 'rgba(16, 16, 16, .8)']
            ]
        },
        borderWidth: 0,
        style: {
            color: '#FFF'
        }
    },


    plotOptions: {
        line: {
            dataLabels: {
                color: '#CCC'
            },
            marker: {
                lineColor: '#333'
            }
        },
        spline: {
            marker: {
                lineColor: '#333'
            }
        },
        scatter: {
            marker: {
                lineColor: '#333'
            }
        },
        candlestick: {
            lineColor: 'white'
        }
    },

    toolbar: {
        itemStyle: {
            color: '#CCC'
        }
    },

    navigation: {
        buttonOptions: {
            backgroundColor: {
                linearGradient: [0, 0, 0, 20],
                stops: [
                    [0.4, '#606060'],
                    [0.6, '#333333']
                ]
            },
            borderColor: '#000000',
            symbolStroke: '#C0C0C0',
            hoverSymbolStroke: '#FFFFFF'
        }
    },

    exporting: {
        buttons: {
            exportButton: {
                symbolFill: '#55BE3B'
            },
            printButton: {
                symbolFill: '#7797BE'
            }
        }
    },

    // scroll charts
    rangeSelector: {
        buttonTheme: {
            fill: {
                linearGradient: [0, 0, 0, 20],
                stops: [
                    [0.4, '#888'],
                    [0.6, '#555']
                ]
            },
            stroke: '#000000',
            style: {
                color: '#CCC',
                fontWeight: 'bold'
            },
            states: {
                hover: {
                    fill: {
                        linearGradient: [0, 0, 0, 20],
                        stops: [
                            [0.4, '#BBB'],
                            [0.6, '#888']
                        ]
                    },
                    stroke: '#000000',
                    style: {
                        color: 'white'
                    }
                },
                select: {
                    fill: {
                        linearGradient: [0, 0, 0, 20],
                        stops: [
                            [0.1, '#000'],
                            [0.3, '#333']
                        ]
                    },
                    stroke: '#000000',
                    style: {
                        color: 'yellow'
                    }
                }
            }
        },
        inputStyle: {
            backgroundColor: '#333',
            color: 'silver'
        },
        labelStyle: {
            color: 'silver'
        }
    },

    navigator: {
        handles: {
            backgroundColor: '#666',
            borderColor: '#AAA'
        },
        outlineColor: '#CCC',
        maskFill: 'rgba(16, 16, 16, 0.5)',
        series: {
            color: '#7798BF',
            lineColor: '#A6C7ED'
        }
    },

    scrollbar: {
        barBackgroundColor: {
            linearGradient: [0, 0, 0, 20],
            stops: [
                [0.4, '#888'],
                [0.6, '#555']
            ]
        },
        barBorderColor: '#CCC',
        buttonArrowColor: '#CCC',
        buttonBackgroundColor: {
            linearGradient: [0, 0, 0, 20],
            stops: [
                [0.4, '#888'],
                [0.6, '#555']
            ]
        },
        buttonBorderColor: '#CCC',
        rifleColor: '#FFF',
        trackBackgroundColor: {
            linearGradient: [0, 0, 0, 10],
            stops: [
                [0, '#000'],
                [1, '#333']
            ]
        },
        trackBorderColor: '#666'
    },

    // special colors for some of the demo examples
    legendBackgroundColor: 'rgba(48, 48, 48, 0.8)',
    legendBackgroundColorSolid: 'rgb(70, 70, 70)',
    dataLabelsColor: '#444',
    textColor: '#E0E0E0',
    maskColor: 'rgba(255,255,255,0.3)'
};

// Apply the theme
var highchartsOptions = Highcharts.setOptions(Highcharts.theme);

var Metrics = new Class({

    Implements: [Options, Events],

    options: {
        wrapper: 'div.metrics',
        urlRoot: 'proxy/?url=',
        url: 'http://localhost:4600/metrics',
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

var m = new Metrics(document.id('metrics'));
