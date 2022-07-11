$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
       CONTEXT_PATH +"/discuss/add",
       {
       "title":title,"content":content
       },
       function(data){
       data = $.parseJSON(data);
       $("#hintBody").text(data.Msg);
       $("#hintModal").modal("show");
       setTimeout(function(){
        if(data.code == 0){
            window.location.reload();
        }
       		$("#hintModal").modal("hide");
       	}, 2000);
       }
	)

}