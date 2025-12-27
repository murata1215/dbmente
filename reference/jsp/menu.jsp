<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="../../tld/lafittag.tld" prefix="lafit"%>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<html>
<head>
	<title>Agnes -MENU-</title>
<link rel="stylesheet" href="../css/frame.css" type="text/css">
<!--[if IE 6]>
<link rel="stylesheet" href="../css/ie6.css" type="text/css">
<![endif]-->
<link rel="stylesheet" href="../css/form.css" type="text/css">
<link rel="stylesheet" href="../css/textstyle_m.css" type="text/css" title="middle">
<link rel="alternate stylesheet" href="../css/textstyle_l.css" type="text/css" title="large">
<link rel="alternate stylesheet" href="../css/textstyle_s.css" type="text/css" title="small">
<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery.hotkeys.js"></script>
<script type="text/javascript" src="../js/include.js"></script>
<script type="text/javascript" src="../js/fontsize.js"></script>
<script language="JavaScript" src="../js/function.js"></script>

	<style type="text/css">
	    .foo {
	        width: 33%;
	        height: 490px;
	        
	        background: #e8eefa;
	        text-align: left;
	        float: left;
	        border-style: solid;
	        border-width: 1px;
	        border-color: #999999;
	    }
	</style>
	<style type="text/css">
		.D1  {width:33%}
		.D2  {width:33%}
		.D3  {width:34%}
	</style>
	
</head>

<body onLoad="focusError()">

<f:view>
<div id="wrap">
<!-- ヘッダー -->
<div id="header"><jsp:include page="../Common/atg_header.jsp" flush="true"><jsp:param name="img_path" value="../img" /></jsp:include></div>
<div id="content-wrap">
<div id="content">
<!-- メイン　ここから -->
<h:form id="frm1">

<lafit:outputSpan id="block11" start="start" lafitrendered="#{login.loginAutoPush != '1'}"/>
<!-- オートログインエリア End -->

  <!-- ボディエリア Start -->
  <div class="menuBody">

	<h:messages/>
	
	<center>
		<h:outputText value="#{menu.title}" style="text-align:center;" styleClass="menuTitle"/>
	</center>

	<div class="foo">
		<h:dataTable var="mnList" value="#{menuDispList1}" style="width:100%" first="0" rows="25" columnClasses="D1,D2,D3">
			<h:column>
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 4 || mnList.lkb == 0}" styleClass="menuHeaderBold" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 3 || mnList.lkb == 0}" styleClass="menuHeader" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 2 && mnList.stlimit != 0}" styleClass="menuLimitHeader" />
				<h:commandLink value="#{mnList.mtitle}" action="#{menu.link}" title="#{mnList.alt}" rendered="#{mnList.lkb == 1 || (mnList.lkb == 2 && mnList.stlimit == 0)}" styleClass="menuLink" >
					<f:param name="#{mnList.paramName[0]}" value="#{mnList.paramValue[0]}"/>
					<f:param name="#{mnList.paramName[1]}" value="#{mnList.paramValue[1]}"/>
					<f:param name="#{mnList.paramName[2]}" value="#{mnList.paramValue[2]}"/>
					<f:param name="#{mnList.paramName[3]}" value="#{mnList.paramValue[3]}"/>
					<f:param name="#{mnList.paramName[4]}" value="#{mnList.paramValue[4]}"/>
					<f:param name="#{mnList.paramName[5]}" value="#{mnList.paramValue[5]}"/>
					<f:param name="#{mnList.paramName[6]}" value="#{mnList.paramValue[6]}"/>
					<f:param name="#{mnList.paramName[7]}" value="#{mnList.paramValue[7]}"/>
					<f:param name="#{mnList.paramName[8]}" value="#{mnList.paramValue[8]}"/>
					<f:param name="#{mnList.paramName[9]}" value="#{mnList.paramValue[9]}"/>
				</h:commandLink>
			</h:column>
		</h:dataTable>
	</div>
	
	<div class="foo">
		<h:dataTable var="mnList" value="#{menuDispList2}" style="width:100%" first="0" rows="25" columnClasses="D1,D2,D3">
			<h:column>
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 4 || mnList.lkb == 0}" styleClass="menuHeaderBold" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 3 || mnList.lkb == 0}" styleClass="menuHeader" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 2 && mnList.stlimit != 0}" styleClass="menuLimitHeader" />
				<h:commandLink value="#{mnList.mtitle}" action="#{menu.link2}" title="#{mnList.alt}" rendered="#{mnList.lkb == 1 || (mnList.lkb == 2 && mnList.stlimit == 0)}" styleClass="menuLink" >
					<f:param name="#{mnList.paramName[0]}" value="#{mnList.paramValue[0]}"/>
					<f:param name="#{mnList.paramName[1]}" value="#{mnList.paramValue[1]}"/>
					<f:param name="#{mnList.paramName[2]}" value="#{mnList.paramValue[2]}"/>
					<f:param name="#{mnList.paramName[3]}" value="#{mnList.paramValue[3]}"/>
					<f:param name="#{mnList.paramName[4]}" value="#{mnList.paramValue[4]}"/>
					<f:param name="#{mnList.paramName[5]}" value="#{mnList.paramValue[5]}"/>
					<f:param name="#{mnList.paramName[6]}" value="#{mnList.paramValue[6]}"/>
					<f:param name="#{mnList.paramName[7]}" value="#{mnList.paramValue[7]}"/>
					<f:param name="#{mnList.paramName[8]}" value="#{mnList.paramValue[8]}"/>
					<f:param name="#{mnList.paramName[9]}" value="#{mnList.paramValue[9]}"/>
				</h:commandLink>
			</h:column>
		</h:dataTable>
	</div>
	
	<div class="foo">
		<h:dataTable var="mnList" value="#{menuDispList3}" style="width:100%" first="0" rows="25" columnClasses="D1,D2,D3">
			<h:column>
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 4 || mnList.lkb == 0}" styleClass="menuHeaderBold" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 3 || mnList.lkb == 0}" styleClass="menuHeader" />
				<h:outputText value="#{mnList.mtitle}" rendered="#{mnList.lkb == 2 && mnList.stlimit != 0}" styleClass="menuLimitHeader" />
				<h:commandLink value="#{mnList.mtitle}" action="#{menu.link3}" title="#{mnList.alt}" rendered="#{mnList.lkb == 1 || (mnList.lkb == 2 && mnList.stlimit == 0)}" styleClass="menuLink" >
					<f:param name="#{mnList.paramName[0]}" value="#{mnList.paramValue[0]}"/>
					<f:param name="#{mnList.paramName[1]}" value="#{mnList.paramValue[1]}"/>
					<f:param name="#{mnList.paramName[2]}" value="#{mnList.paramValue[2]}"/>
					<f:param name="#{mnList.paramName[3]}" value="#{mnList.paramValue[3]}"/>
					<f:param name="#{mnList.paramName[4]}" value="#{mnList.paramValue[4]}"/>
					<f:param name="#{mnList.paramName[5]}" value="#{mnList.paramValue[5]}"/>
					<f:param name="#{mnList.paramName[6]}" value="#{mnList.paramValue[6]}"/>
					<f:param name="#{mnList.paramName[7]}" value="#{mnList.paramValue[7]}"/>
					<f:param name="#{mnList.paramName[8]}" value="#{mnList.paramValue[8]}"/>
					<f:param name="#{mnList.paramName[9]}" value="#{mnList.paramValue[9]}"/>
				</h:commandLink>
			</h:column>
		</h:dataTable>
	</div>
  </div>
  
  <!-- ボディエリア End -->

  <!-- メインボタンエリア Start -->
  <div class="button" style="height: 5px; overflow: hidden; display: none;">
	<h:commandButton type="submit" value="戻る" id="prevButton" action="#{menu.back}" disabled="#{!menu.availableBack}" styleClass="button" />
  </div>
  <!-- メインボタンエリア End -->
<input type="hidden" name="frm1:PREV_FLG" id="frm1:PREV_FLG" value="<h:outputText value="#{menu.availableBack}" />">

<lafit:outputSpan id="block12"/>


<span style="display:none">


<!-- 他プロジェクトリンクエリア Start -->
	<h:commandButton type="button" value="業務画面" id="rdeUtilButton" styleClass="button" onclick="oLinkClick();return false;" />
	<h:inputText id="oLink" value="#{menu.otherProjectLink}" />
	<h:inputText id="oPush" value="#{menu.otherProjectPush}" />
<!-- 他プロジェクトリンクエリア End -->

<!-- オートログインエリア Start -->
	<h:commandLink id="LINK" value="#{login.loginParamMenu}" action="#{login.linkInit}" >
	<f:param name="#{login.loginParamParam[0]}" value="#{login.loginParamValue[0]}" />
	<f:param name="#{login.loginParamParam[1]}" value="#{login.loginParamValue[1]}" />
	<f:param name="#{login.loginParamParam[2]}" value="#{login.loginParamValue[2]}" />
	<f:param name="#{login.loginParamParam[3]}" value="#{login.loginParamValue[3]}" />
	<f:param name="#{login.loginParamParam[4]}" value="#{login.loginParamValue[4]}" />
	<f:param name="#{login.loginParamParam[5]}" value="#{login.loginParamValue[5]}" />
	<f:param name="#{login.loginParamParam[6]}" value="#{login.loginParamValue[6]}" />
	<f:param name="#{login.loginParamParam[7]}" value="#{login.loginParamValue[7]}" />
	<f:param name="#{login.loginParamParam[8]}" value="#{login.loginParamValue[8]}" />
	<f:param name="#{login.loginParamParam[9]}" value="#{login.loginParamValue[9]}" />
	</h:commandLink>
	<h:inputText id="AUTOPUSH" value="#{login.loginAutoPush}" />
<!-- オートログインエリア End -->

</span>

</h:form>
<!-- メイン　ここまで -->

</div><!-- end of content -->
</div><!-- end of content-wrap -->
	<!-- ########################### フッタ ここから ########################### -->
    <div id="footer">
	  	<!-- メッセージエリア ここから -->
		<div id="sysmsg">
			<div id="MESSAGE_DIV" class="messageDiv" >

			</div>
        </div><!-- end of sysmsg -->
	  	<!-- メッセージエリア ここまで -->
		<!-- Fnキー設定　ここから -->
        <jsp:include page="../Common/function_buttons.jsp" flush="true">
        	<jsp:param name="textf1" value="　" /><jsp:param name="statf1" value="enable" />
        	<jsp:param name="textf2" value="　" /><jsp:param name="statf2" value="enable" />
        	<jsp:param name="textf3" value="　" /><jsp:param name="statf3" value="enable" />
        	<jsp:param name="textf4" value="　" /><jsp:param name="statf4" value="enable" />
        	<jsp:param name="textf5" value="　" /><jsp:param name="statf5" value="enable" />
        	<jsp:param name="textf6" value="　" /><jsp:param name="statf6" value="enable" />
        	<jsp:param name="textf7" value="　" /><jsp:param name="statf7" value="enable" />
        	<jsp:param name="textf8" value="戻る" /><jsp:param name="statf8" value="enable" />
        	<jsp:param name="textf9" value="　" /><jsp:param name="statf9" value="enable" />
        	<jsp:param name="textf10" value="　" /><jsp:param name="statf10" value="enable" />
        	<jsp:param name="textf11" value="　" /><jsp:param name="statf11" value="enable" />
        	<jsp:param name="textf12" value="　" /><jsp:param name="statf12" value="enable" />
        </jsp:include>
		<!-- Fnキー設定　ここまで -->

    </div><!-- end of footer -->
	<!-- ########################### フッタ ここまで ########################### -->
</div><!-- end of wrap -->

</f:view>

<!-- 他プロジェクトリンクエリア Start -->
	<SCRIPT LANGUAGE="JavaScript">
		<!--
		var scrx = screen.availWidth-12;
		var scry = screen.availHeight-50;
/*
		if (document.all.item(document.forms[0].id + ":oPush").value == "1") {
			document.all.item(document.forms[0].id + ":oPush").value = "";
			var opt1 = "directories=no,location=no,menubar=no,status=yes,toolbar=no,";
			var opt2 = "resizable=yes,scrollbars=yes,width="+scrx+",height="+scry+",";
			var opt3 = "top=0,left=0";
			var win = window.open(document.all.item(document.forms[0].id+":oLink").value,"",opt1+opt2+opt3);
		}
*/
		if (document.getElementById(document.forms[1].id + ":oPush").value == "1") {
			document.getElementById(document.forms[1].id + ":oPush").value = "";
			var opt1 = "directories=no,location=no,menubar=no,status=yes,toolbar=no,";
			var opt2 = "resizable=yes,scrollbars=yes,width="+scrx+",height="+scry+",";
			var opt3 = "top=0,left=0";
			var win = window.open(document.getElementById(document.forms[1].id+":oLink").value,"",opt1+opt2+opt3);
		}
		//-->
	</SCRIPT>
<!-- 他プロジェクトリンクエリア End -->

<!-- オートログインエリア Start -->
<SCRIPT LANGUAGE="JavaScript">
	<!--
//	if (document.all.item(document.forms[0].id + ":AUTOPUSH").value == "1") {
//		document.all.item(document.forms[0].id + ":AUTOPUSH").value = ""
//		document.all.item(document.forms[0].id + ":LINK").click();
//	}
	if (document.getElementById(document.forms[1].id + ":AUTOPUSH").value == "1") {
		document.getElementById(document.forms[1].id + ":AUTOPUSH").value = ""
		document.getElementById(document.forms[1].id + ":LINK").onclick();
	}
	//-->
</SCRIPT>
<!-- オートログインエリア End -->
	

</body>
<!-- ########################### JavaSctipt設定 ここから ########################### -->
<script type="text/javascript"><!--
	var hid = document.forms[0].id;
	var fid = document.forms[1].id;
	var prev_flg = j$("#"+fid+"\\:PREV_FLG").val();

	//******************************************************************
	// 初期化処理
	//******************************************************************
	function init() {
	}


	// ファンクション有効／無効設定
	toggleFn("fn1", "disabled");
	toggleFn("fn2", "disabled");
	toggleFn("fn3", "disabled");
	toggleFn("fn4", "disabled");
	toggleFn("fn5", "disabled");
	toggleFn("fn6", "disabled");
	toggleFn("fn7", "disabled");
	if (prev_flg == 'false') {
		toggleFn("fn8", "disabled");
	}
	toggleFn("fn9", "disabled");
	toggleFn("fn10", "disabled");
	toggleFn("fn11", "disabled");
	toggleFn("fn12", "disabled");

	// ファンクション動作設定
	function f1() {}
	function f2() {}
	function f3() {}
	function f4() {}
	function f5() {}
	function f6() {}
	function f7() {}
	function f8() {
  		j$("#" + fid + "\\:prevButton").trigger("click");
	}
	function f9() {}
	function f10() {}
	function f11() {}
	function f12() {}

	j$("#"+hid+"\\:hdrmain").css("display", "none");
--></script>
<!-- ########################### JavaSctipt設定 ここまで ########################### -->

</html>

