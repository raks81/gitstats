var monthNames = [ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
		"SEP", "OCT", "NOV", "DEC" ];

var loadData = function() {
	// Clear existing data
	$("#compare_table > tbody").html("");

	// Read the table JSON data
	var response = (function() {
		var response = null;
		// var data_url = "http://localhost/data.json";
		var data_url = "rest/ui/data?p="
				+ encodeURIComponent($("#p1").val() + "," + $("#p2").val());
		$('#loading-indicator').show();
		$.ajax({
			'async' : false,
			'global' : false,
			'url' : data_url,
			'success' : function(data) {
				$('#loading-indicator').hide();
				response = data;
			}
		});
		return response;
	})();

	$(function() {
		var trHtml = '';
		$.each(response.rows, function(i, row) {
			trHtml += '<tr>';
			$.each(row.cells, function(j, cell) {
				if (cell.type == 'title') {
					trHtml += '<td><strong>' + cell.data.value
							+ '</strong></td>';
				} else if (cell.type == 'number') {
					trHtml += '<td>' + $.number(cell.data.value) + '</td>';
				} else if (cell.type == 'text') {
					trHtml += '<td>' + cell.data.value + '</td>';
				} else if (cell.type == 'link') {
					trHtml += '<td><a href="' + cell.data.value + '">'
							+ cell.data.value + '<a/></td>';
				} else if (cell.type == 'timeseries' || cell.type == 'donut') {
					trHtml += '<td>' + '<div id="chart_' + i + '_' + j + '"/>'
							+ '</td>';
				}
			});
			trHtml += '</tr>';
			$('#compare_table').append(trHtml);
			trHtml = '';

			$.each(row.cells, function(j, cell) {
				if (cell.type == 'timeseries') {
					cell.data.ts_values.types = JSON.parse('{ "'
							+ cell.data.value + '" : "bar"}');
					cell.data.ts_values.x = "x";

					var chart = c3.generate({
						bindto : "#chart_" + i + '_' + j,
						size : {
							height : 180,
							width : 360
						},
						bar : {
							width : {
								ratio : 0.065
							}
						},
						color : {
							pattern : [ '#ee6933' ]
						},
						point : {
							show : false
						},
						legend : {
							show : false
						},
						data : cell.data.ts_values,
						padding : {
							right : 15
						},
						axis : {
							x : {
								type : 'timeseries',
								tick : {
									format : '%d-%b-%Y',
									count : 5,
									culling : {
										max : 5
									}
								}
							},
							y : {
								tick : {
									format : function(x) {
										return Math.round(x);
									},
									count : 3,
									culling : {
										max : 3
									}
								}
							}
						}
					});
				} else if (cell.type == 'donut') {
					cell.data.ts_values.type = 'donut';
					var chart = c3.generate({
						bindto : "#chart_" + i + '_' + j,
						size : {
							height : 180,
							width : 360
						},
						legend : {
							show : true
						},
						donut : {
							label : {
								show : false
							}
						},
						data : cell.data.ts_values,
					});
				}
			});
		});
	});
}

$(document).ready(function() {
	$('#b_stats').on('click', loadData);
});
