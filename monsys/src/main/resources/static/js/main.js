function updateHostTable() {
    $.getJSON('/hosts', function (data) {

        var rowData = [];

        jQuery(data).each(function(i, hostEntity){

            var statusColor;

            if (hostEntity.up === false) {
                statusColor = "red";
            }
            else {
                var portStatus = [];

                jQuery(hostEntity.ports).each(function (i, portEntity) {
                    portStatus.push(portEntity.up);
                });

                if (jQuery.inArray(false, portStatus))
                    statusColor = "yellow";
                else
                    statusColor = "green";
            }

            if ($("#hTable #" + hostEntity.id).hasClass("activeRow")){
                showPortsForHost(hostEntity.id);
                addRow("activeRow");
            }
            else addRow("");

            function addRow(activeRowClass) {
                rowData.push("<tr id='" + hostEntity.id + "' class='" + activeRowClass + "' >" +
                    "<th scope='row'>" + hostEntity.ipAddress + "</th>" +
                    "<td><i>" + hostEntity.domainName + "</i></td>" +
                    "<td>\n" +
                    "<svg height=\"20\" width=\"20\">\n" +
                    "<circle cx=\"10\" cy=\"10\" r=\"8\" stroke-width=\"1\" fill=\"" + statusColor + "\" />\n" +
                    "</svg>\n" +
                    "</td>" +
                    "<td><a href=\"#\">Удалить</a></td>" +
                    "</tr>")
            }


        });

        $("#hTable tbody tr").remove();
        $("#hTable tbody").append(rowData.join(""))
    });
}

function updateMonitoringHostTable() {
    $.getJSON('/hosts/monitoring', function (data) {

        var rowData = [];

        jQuery(data).each(function(i, mHostEntity){
            rowData.push("<tr id='" + mHostEntity.id + "' >" +
                "<th scope='row'>" + mHostEntity.ipAddress + "</th>" +
                "<td>" + mHostEntity.targets.length + "</td>" +
                "<td><a data-toggle=\"modal\" data-target=\"#monitoringSettings\" href=\"#\">Подробности</a></td>" +
                "</tr> ")
        });

        $("#mTable tbody tr").remove();
        $("#mTable tbody").append(rowData.join(""));
    });
}

function showPortsForHost(id) {
    $.getJSON('/ports/' + id, function (data) {

        var portRow = [];

        jQuery(data).each(function (i, portEntity) {

            var statusColor;

            if (portEntity.up)
                statusColor = "green";
            else
                statusColor = "red";

            portRow.push("<tr>" +
                "<th scope='row'>" + portEntity.number + "</th>" +
                "<td>" + portEntity.sevice + "</td>" +
                "<td>" +
                "<svg height=\"20\" width=\"20\">\n" +
                "<circle cx=\"10\" cy=\"10\" r=\"8\" stroke-width=\"1\" fill=\"" + statusColor + "\" />\n" +
                "</svg>" +
                "</td>" +
                "<td><a href='#'>Удалить</a></td>" +
                "</tr>"
            );

        });

        console.log(data);

        $("#pTable tbody tr").remove();
        $("#pTable tbody").append(portRow.join(""));

    });
}

$(document).on("click", "#hTable tbody tr", function () {
    if ($(this).hasClass("activeRow")) {
        $(this).removeClass("activeRow");
    }
    else {
        $("#hTable tbody tr").removeClass("activeRow");
        $(this).addClass("activeRow");
        showPortsForHost( $(this).attr('id'));
    }
});

// $(document).on ("click", "#mTable tbody tr a", function () {
//     var modalRowData = [];
//
//     jQuery(mHostEntity.targets).each(function (i, target) {
//         modalRowData.push("<tr>" + target.ipAddress + "</tr>");
//     })
//
//     $("#modalTable tbody tr").remove();
//     $("#modalTable tbody").append(modalRowData.join(""));
// });

intervalId = setInterval(updateHostTable, 3000);
intervalId = setInterval(updateMonitoringHostTable, 3000);
