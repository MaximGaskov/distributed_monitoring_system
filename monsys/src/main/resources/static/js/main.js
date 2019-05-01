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

            rowData.push("<tr>" +
                "<th scope='row'>" + mHostEntity.ipAddress + "</th>" +
                "<td>" + mHostEntity.targets.length + "</td>" +
                "<td><a id='" + mHostEntity.id + "' data-toggle=\"modal\" " +
                "data-target=\"#monitoringSettings\" href=\"#\">Подробности</a></td>" +
                "</tr>")

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

            portRow.push("<tr id='" + portEntity.id + "'>" +
                "<th scope='row'>" + portEntity.number + "</th>" +
                "<td><i>" + portEntity.service + "</i></td>" +
                "<td>" +
                "<svg height=\"20\" width=\"20\">\n" +
                "<circle cx=\"10\" cy=\"10\" r=\"8\" stroke-width=\"1\" fill=\"" + statusColor + "\" />\n" +
                "</svg>" +
                "</td>" +
                "<td><a href='#'>Удалить</a></td>" +
                "</tr>"
            );

        });

        $("#pTable tbody tr").remove();
        $("#pTable tbody").append(portRow.join(""));

    });
}

function updateModalContent(monitoringHostId) {
    console.log(monitoringHostId);
    $.getJSON('/hosts/monitoring/' + monitoringHostId, function (data) {

        var hostRow = [];
        console.log(data);

        jQuery(data.targets).each(function (i, hostEntity) {

            hostRow.push("<tr>" +
                "<td>" + hostEntity.ipAddress + "</td>" +
                "</tr>"
            );
        });


        $("#modalTable tbody tr").remove();
        $("#modalTable tbody").append(hostRow.join(""));

    });
}

$(document).on("click", "#hTable tbody tr", function () {
    if ($(this).hasClass("activeRow")) {
        $(this).removeClass("activeRow");
        $("#pTable tbody tr").remove();
        $("#portsTableLabel").html("Список портов");
        chosenHostId = 0;
    }
    else {
        $("#hTable tbody tr").removeClass("activeRow");
        $(this).addClass("activeRow");
        showPortsForHost( $(this).attr('id'));
        $("#portsTableLabel").html("Список портов " + $(this).find("th").text());
        chosenHostId = $(this).attr('id');
    }
});


var activeMonitoringHost;
$(document).on("click", "#mTable tbody a", function () {
    $("#modalLabel").html("Цели хоста мониторинга <i>" + $(this).parent().parent().find("th").text() + "</i>");
    updateModalContent( $(this).attr('id'));
    activeMonitoringHost = $(this).attr('id');
});

$('#hostIpForm').submit(function(e){
    e.preventDefault();
    $.ajax({
        url:'/hosts',
        type:'post',
        data:$('#hostIpForm').serialize(),
        success:function() {
            updateHostTable();
            updateMonitoringHostTable();
            $("#hostIpValidationMsg").text('');
        },
        error: function (data) {
            $("#hostIpValidationMsg").text(data.responseText);
        }
    });
});

var chosenHostId;

$('#portForm').submit(function(e){
    e.preventDefault();
    var dataToSend = $('#portForm').serialize() + "&hostId=" + chosenHostId;
    if(chosenHostId !== 0) {
        $.ajax({
            url:'/ports',
            type:'post',
            data: dataToSend,
            success:function() {
                showPortsForHost(chosenHostId);
                $("#hostIpValidationMsg").text('');
            },
            error: function (data) {
                $("#portValidationMsg").text(data.responseText);
            }
        });
    }
});

$('#monitoringForm').submit(function(e){
    e.preventDefault();
    $.ajax({
        url:'/hosts/monitoring',
        type:'post',
        data:$(this).serialize(),
        success:function() {
            updateMonitoringHostTable();
            $("#monitoringValidationMsg").text('');
        },
        error: function (data) {
            $("#monitoringValidationMsg").text(data.responseText);
        }
    });
});

$(document).on("click", "#hTable tbody a", function (e) {

    e.preventDefault();
    $.ajax({
        url:'/hosts/' + $(this).parent().parent().attr('id'),
        type: 'delete',
        success: function () {
            updateHostTable();
            showPortsForHost('');
            $("#portsTableLabel").html("Список портов");

            updateMonitoringHostTable();
        }
    })

});

$(document).on("click", "#pTable tbody a", function (e) {

    e.preventDefault();
    $.ajax({
        url:'/ports/' + $(this).parent().parent().attr('id'),
        type: 'delete',
        success: function () {
            showPortsForHost(chosenHostId);
        }
    })

});

$("#mhostDelete").click(function () {
    $.ajax({
        url:'/hosts/monitoring/' + activeMonitoringHost,
        type: 'delete',
        success: function () {
            updateMonitoringHostTable();
            $("#monitoringValidationMsg").text('');
        },
        error: function (data) {
            $("#monitoringValidationMsg").text(data.responseText);
        }
    })
});


intervalId = setInterval(updateHostTable, 30000);
intervalId = setInterval(updateMonitoringHostTable, 30000);
