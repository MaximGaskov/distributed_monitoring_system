function updateHostTable() {
    $.getJSON('/hosts', function (data) {


        var rowData = [];

        jQuery(data).each(function(i, mHostEntity){
            console.log(mHostEntity.id, mHostEntity.ipAddress)

            var statusColor;

            if (mHostEntity.up === false) {
                statusColor = "red";
            }
            else {
                console.log("not red")

                var portStatus = [];

                jQuery(mHostEntity.ports).each(function (i, portEntity) {
                    portStatus.push(portEntity.up);
                })

                if (jQuery.inArray(false, portStatus))
                    statusColor = "yellow";
                else
                    statusColor = "green";
            }


            rowData.push("<tr id='>" + mHostEntity.id + "'>" +
                "<th scope='row'>" + mHostEntity.ipAddress + "</th>" +
                "<td><i>" + mHostEntity.domainName + "</i></td>" +
                "<td>\n" +
                    "<svg height=\"20\" width=\"20\">\n" +
                    "<circle cx=\"10\" cy=\"10\" r=\"8\" stroke-width=\"1\" fill=\"" + statusColor + "\" />\n" +
                    "</svg>\n" +
                "</td>" +
                "<td><a href=\"#\">Удалить</a></td>" +
                "</tr> " +
                "")
        })

        $("#hTable tbody tr").remove()
        $("#hTable tbody").append(rowData.join(""))
        console.log(rowData.join(""))
    });
}

intervalId = setInterval(updateHostTable, 3000);