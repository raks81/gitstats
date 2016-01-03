
$(document).ready(
		function() {
			
			//Read the table JSON data
			var response = (function () {
				var response = null;
				var data_url = "http://localhost/data.json";
				$.ajax({
					'async': false,
					'global': false,
					'url': data_url,
					'success': function (data) {
						response = data;
					}
				});
				return response;
			})(); 
			
			// convert string to JSON
			//response = $.parseJSON(response);

			$(function () {
				var trHtml = '';
				$.each(response.rows, function (i, row) {
					trHtml += '<tr>';
					$.each(row.cells.cell_data, function (j, cell) {
						if(cell.type == 'title') {
							trHtml += '<td>' + cell.data.value + '</td>';
						} else if (cell.type == 'number') {
							trHtml += '<td>' + cell.data.value + '</td>';
						} else if (cell.type == 'timeseries') {
							trHtml += '<td>' + '<div id="chart_'+ i+'_'+j + '"/>' + '</td>';
						}
					});
					trHtml += '</tr>';
					$('#compare_table').append(trHtml);
					trHtml = '';
					
					$.each(row.cells.cell_data, function (j, cell) {
						if (cell.type == 'timeseries') {
							var chart = c3.generate({
								bindto: "#chart_" + i+'_'+j,
								size: {
									height: 240,
									width: 480
								},
								point: {
									  show: false
								},
								legend: {
									  show: false
									},
								data: cell.data.values,
								axis: {
									x: {
										type: 'timeseries',
										tick: {
											format:  function (x) { return x.getFullYear(); },
											count: 5,
											culling: {
										        max: 5
										      }
										}
									}
								}
							});
						}
					});
				});
			});
		});