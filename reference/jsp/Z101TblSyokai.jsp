<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="../../tld/lafittag.tld" prefix="lafit"%>
<!-- ########################### 定義エリア ここから ########################### -->
<html>
<meta http-equiv="pragma" content="non-cache">
<meta http-equiv="cache-control" content="non-cache">
<meta http-equiv="expires" content="0">
<head>
<title>テーブル照会</title>
<link rel="stylesheet" href="../../css/frame.css" type="text/css">
<!--[if IE 6]>
<link rel="stylesheet" href="../../css/ie6.css" type="text/css">
<![endif]-->
<link rel="stylesheet" href="../../css/form.css" type="text/css">
<link rel="stylesheet" href="../../css/textstyle_m.css" type="text/css" title="middle">
<link rel="alternate stylesheet" href="../../css/textstyle_l.css" type="text/css" title="large">
<link rel="alternate stylesheet" href="../../css/textstyle_s.css" type="text/css" title="small">
<script type="text/javascript" src="../../js/jquery.js"></script>
<script type="text/javascript" src="../../js/jquery.hotkeys.js"></script>
<script type="text/javascript" src="../../js/include.js"></script>
<script type="text/javascript" src="../../js/fontsize.js"></script>
<script type="text/javascript" src="../../js/function.js"></script>
<!--  リスト部の項目幅を設定 -->
<style TYPE="text/css">
<!--
	.listTable1 {width: 32px;}
	.listTable2 {width: 51px;}
	.listTable3 {width: 101px;}
	.listTable4 {width: 62px;}
	.listTable5 {width: 82px;}
	.listTable6 {width: 101px;}
	.listTable7 {width: 201px;}
	.listTable8 {width: 151px;}
-->
</style>	
</head>
<body onLoad="focusError();">
<jsp:include page="../../Common/senddata.jsp" flush="true" />
<f:view>
<div id="ALL_DIV" style="display:none">
<div id="wrap">
<!-- ヘッダー -->
<div id="header"><jsp:include page="../../Common/atg_header.jsp" flush="true"><jsp:param name="img_path" value="../../img" /></jsp:include></div>
<div id="content-wrap">
<div id="content">
<!-- メイン　ここから -->
<h:form id="frm1">
<input type="hidden" value="Z101TblSyokai" name="fromOutcome" />
<!-- ########################### 定義エリア ここまで ########################### -->
<div id="BODY_DIV" class="body" >
<!-- 条件指定部表示／非表示リンク -->
<span style="position:relative; left:895px;">
	<a href="javascript:toggleSearchCondition('searchConditionArea')">
	<img id="toggleImage" src="../../img/triangle_down.gif" border="0" style="vertical-align:bottom" />条件指定</a>
</span>
<%-- 次へボタン自動押下 --%>
<lafit:hiddenNext value="#{Z101TblSyokai.showMenu}" id="auto" autoButtonId="nextButton"/>

	<!-- ########################### 検索条件 ここから ########################### -->
	<div id="searchConditionArea" class="blockBody">
		<table class="layoutTable"><tr>
			<td class="firstCaption">システム区分</td>
			<td class="value">
				<h:selectOneMenu id="sys" value="#{Z101TblSyokai.sys}" style="width:140px;" >
					<f:selectItems value="#{formComponentUtilsKc.select['SQLZ003,001,BLANK']}" />
				</h:selectOneMenu>
			</td>
			<td class="caption">種別</td>
			<td class="value">
				<h:selectOneMenu id="shu" value="#{Z101TblSyokai.shu}" style="width:140px;" >
					<f:selectItems value="#{formComponentUtilsKc.select['SQLZ003,002,BLANK']}" />
				</h:selectOneMenu>
			</td>
		</tr></table>
		<table class="layoutTable"><tr>
			<td class="firstCaption">テーブルID</td>
			<td class="value">
				<h:inputText id="tblnm" value="#{Z101TblSyokai.tblnm}" style="width:200px; ime-mode:inactive;"
							maxlength="20" onblur="checkInputElementFormat(this,['hankaku'],'')"
							onkeyup="toUpper(this);focusNext(this.form, this, true, event);"
							accesskey="#{Z101TblSyokai.screenErrors['tblnm']}"/>
			</td>
			<td class="caption">テーブル名</td>
			<td class="value">
				<h:inputText id="tbljnm" value="#{Z101TblSyokai.tbljnm}" style="width:200px; ime-mode:active; "
						maxlength="40" onblur="checkInputElementFormat(this,['zenkaku'],'')"
						onkeyup="focusNext(this.form, this, true, event);"
						accesskey="#{Z101TblSyokai.screenErrors['tbljnm']}" />
			</td>
			</tr><tr>
			<td class="firstCaption">項目ID</td>
			<td class="value">
				<h:inputText id="clmnm" value="#{Z101TblSyokai.clmnm}" style="width:200px; ime-mode:inactive;"
							maxlength="20" onblur="checkInputElementFormat(this,['hankaku'],'')"
							onkeyup="toUpper(this);focusNext(this.form, this, true, event);"
							accesskey="#{Z101TblSyokai.screenErrors['clmnm']}"
							disabled=""/>
			</td>
			<td class="caption">項目名</td>
			<td class="value">
				<h:inputText id="clmjnm" value="#{Z101TblSyokai.clmjnm}" style="width:200px;ime-mode:active; "
						maxlength="40" onblur="checkInputElementFormat(this,['zenkaku'],'')"
						onkeyup="focusNext(this.form, this, true, event);"
						accesskey="#{Z101TblSyokai.screenErrors['clmjnm']}" />
			</td>
		</tr></table>
	</div>
	<!-- ########################### 検索条件 ここまで ########################### -->
		
	<!-- ########################### 結果リスト ここから ########################### -->
	<lafit:outputSpan id="list1Start" start="start" lafitrendered="#{!Z101TblSyokai.clmList == 'true'}" />
	<div class="blockBody" id="searchResultBlockBody">
		
		<!-- ページ見出し -->
		<lafit:outputSpan id="pageViewStart" start="start" lafitrendered="#{Z101TblSyokaiList.rowCount > 0}" />
		<div class="pageHeader" id="pageHeader" style="width:951px;">
			<h:outputText value="データの最初" rendered="#{Z101TblSyokai.pageCnt == 0 && Z101TblSyokai.hasMore == 'true'}" />
			<h:outputText value="データの途中" rendered="#{Z101TblSyokai.pageCnt != 0 && Z101TblSyokai.hasMore == 'true'}" />
			<h:outputText value="データの終わり" rendered="#{Z101TblSyokai.hasMore == 'false'}" />
		</div>
		<lafit:outputSpan id="pageViewEnd" />
			
		<!-- ヘッダ部 Start -->
		<div class="tableHeader" id="searchResultHeader" style="width:951px;">
			<table class="listHeader" style="width: 951px; table-layout: fixed;"><tr>
				<td class="listTable1" style="text-align:center;">選</td>
				<td class="listTable2" style="text-align:center;">区分</td>
				<td class="listTable3" style="text-align:center;">名称</td>
				<td class="listTable4" style="text-align:center;">種別</td>
				<td class="listTable5" style="text-align:center;">種別名称</td>
				<td class="listTable6" style="text-align:center;">テーブル名</td>
				<td class="listTable7" style="text-align:center;">テーブル日本語名</td>
			</tr></table>
		</div>
		<!-- ヘッダ部 End -->
		        		
	 	<!-- リスト部 Start -->
		<div class="tableData" id="resultListArea" style="width:967px; height:430px; overflow:scroll; top:-15px;"
							onScroll="j$('#searchResultHeader').scrollLeft(this.scrollLeft);">
			<h:dataTable id="list" value="#{Z101TblSyokaiList}" var="list" 
					 rendered="#{Z101TblSyokaiList.rowCount > 0}" 
					 border="0" 
					 styleClass="listView" 
					 rowClasses="tblOddRow,tblEvenRow"
					 style="width: 951px; table-layout: fixed;"
					 columnClasses="listTable1 base,listTable2 base,listTable3 base,listTable4 base,listTable5 base,listTable6 base,listTable7 base">
		
			<% /* 選 */ %>
			<h:column>
				<f:verbatim>
					<input type="radio" value="" name="selradio"/>
				</f:verbatim>
				<%-- テーブルメンテナンス・修正 --%>
				<h:commandLink id="Select1" value="select" style="display:none" action="#{Z102TblMente.init}">
					<f:param name="viewMode" value="M040000001"/>
					<f:param name="businessKbn" value="2"/>
					<f:param name="fromOutcome" value="Z101TblSyokai" />
		 			<f:param name="gparam" value="2!#{list.tblnm}" />	
				</h:commandLink>
				<%-- テーブルメンテナンス・照会 --%>
				<h:commandLink id="Select2" value="select" style="display:none" action="#{Z102TblMente.init}">
					<f:param name="viewMode" value="M040000001"/>
					<f:param name="businessKbn" value="4"/>
					<f:param name="fromOutcome" value="Z101TblSyokai" />
		 			<f:param name="gparam" value="4!#{list.tblnm}" />	
				</h:commandLink>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* システム区分 */ %>
				<h:outputText value="#{list.sys}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* システム名称 */ %>
				<h:outputText value="#{list.sysmei}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 種別 */ %>
				<h:outputText value="#{list.shu}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 種別名称 */ %>
				<h:outputText value="#{list.shumei}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* テーブル名 */ %>
				<h:outputText value="#{list.tblnm}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* テーブル日本語名 */ %>
				<h:outputText value="#{list.tbljnm}" />
			</h:panelGrid>
			</h:column>

			</h:dataTable>
	
		</div>
	 	<!-- リスト部 END -->
		</div>
	<lafit:outputSpan id="list1End" />
	
	
	<lafit:outputSpan id="list2Start" start="start" lafitrendered="#{Z101TblSyokai.clmList == 'true'}" />
	<div class="blockBody" id="searchResultBlockBody">
		
		
		<!-- ヘッダ部 Start -->
		<div class="tableHeader" id="searchResultHeader" style="width:951px;">
			<table class="listHeader" style="width: 951px; table-layout: fixed;"><tr>
				<td class="listTable1" style="text-align:center;">選</td>
				<td class="listTable6" style="text-align:center;">テーブル名</td>
				<td class="listTable7" style="text-align:center;">テーブル名称</td>
				<td class="listTable6" style="text-align:center;">項目名</td>
				<td class="listTable7" style="text-align:center;">項目名称</td>
				<td class="listTable2" style="text-align:center;">型</td>
				<td class="listTable2" style="text-align:center;">長さ１</td>
				<td class="listTable2" style="text-align:center;">長さ２</td>
			</tr></table>
		</div>
		<!-- ヘッダ部 End -->
		
		        		
	 	<!-- リスト部 Start -->
		<div class="tableData" id="resultListArea" style="width:967px; height:430px; overflow:scroll; top:-15px;"
							onScroll="document.all.searchResultHeader.scrollLeft=this.scrollLeft;">
			<h:dataTable id="list2" value="#{Z101TblSyokaiList}" var="list2" 
					 rendered="#{Z101TblSyokaiList.rowCount > 0}" 
					 border="0" 
					 styleClass="listView" 
					 rowClasses="tblOddRow,tblEvenRow"
					 style="width: 951px; table-layout: fixed;"
					 columnClasses="listTable1 base,listTable6 base,listTable7 base,listTable6 base,listTable7 base,listTable2 base,listTable2 base,listTable2 base">
		
			<% /* 選 */ %>
			<h:column>
				<f:verbatim>
					<input type="radio" value="" name="selradio2"/>
				</f:verbatim>
				<%-- テーブルメンテナンス・修正 --%>
				<h:commandLink id="Select3" value="select" style="display:none" action="#{Z102TblMente.init}">
					<f:param name="viewMode" value="M040000001"/>
					<f:param name="businessKbn" value="2"/>
					<f:param name="fromOutcome" value="Z101TblSyokai" />
		 			<f:param name="gparam" value="2!#{list2.tblnm}" />	
				</h:commandLink>
				<%-- テーブルメンテナンス・照会 --%>
				<h:commandLink id="Select4" value="select" style="display:none" action="#{Z102TblMente.init}">
					<f:param name="viewMode" value="M040000001"/>
					<f:param name="businessKbn" value="4"/>
					<f:param name="fromOutcome" value="Z101TblSyokai" />
		 			<f:param name="gparam" value="4!#{list2.tblnm}" />	
				</h:commandLink>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* テーブル名 */ %>
				<h:outputText value="#{list2.tblnm}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* テーブル日本語名 */ %>
				<h:outputText value="#{list2.tbljnm}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 項目名 */ %>
				<h:outputText value="#{list2.clmnm}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 項目名称 */ %>
				<h:outputText value="#{list2.clmjnm}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 型 */ %>
				<h:outputText value="#{list2.kata}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 長さ１ */ %>
				<h:outputText value="#{list2.lng1}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 長さ２ */ %>
				<h:outputText value="#{list2.lng2}" />
			</h:panelGrid>
			</h:column>
	
			</h:dataTable>
	
		</div>
	 	<!-- リスト部 END -->
		</div>
	<lafit:outputSpan id="list2End" />
	<!-- ########################### 結果リスト ここまで ########################### -->

	<!-- ########################### ボタンエリア ここから ########################### -->
	<div style="display:none;">
		<!-- /*各処理区分に応じた次へボタン押下時のコマンド用*/ -->
		<h:commandButton value="検索" id="searchButton" styleClass="button" action="#{Z101TblSyokai.find}" />
		<h:commandButton value="戻る" id="returnButton" styleClass="button" action="#{Z101TblSyokai.cancel}"/>
		<h:commandButton value="詳細" id="MeisaiButton1" styleClass="button" type="button" onclick="MeisaiAction('1');return false;" rendered="#{Z101TblSyokaiList.rowCount > 0}" />
		<h:commandButton value="修正" id="MeisaiButton2" styleClass="button" type="button" onclick="MeisaiAction('2');return false;" rendered="#{Z101TblSyokaiList.rowCount > 0}" />
		<h:commandButton value="前頁" id="movePrev" styleClass="button" type="button" action="#{Z101TblSyokai.findPrevP}" />
		<h:commandButton value="次頁" id="moveNext" styleClass="button" type="button" action="#{Z101TblSyokai.findNextP}" />
		<lafit:commandDownLoad value="ＣＳＶ" id="csv" styleClass="mainButton" 
			style="width:100px;display:none;" action="#{Z101TblSyokai.csvDownload}" >
			<f:param name="DLFIND_PATTERN" value=""/>
			<f:param name="DLFILENM_PARAM" value=""/>
			<f:param name="DLWHERE_PARAM"  value=""/>
		</lafit:commandDownLoad>
		<h:inputHidden id="toggle" value="#{Z101TblSyokai.toggle}" />
		<h:inputHidden id="transition" value="#{Z101TblSyokai.transition}" />
		<h:inputHidden id="csvKbn" value="#{Z101TblSyokai.csvKbn}"/>
		<h:inputHidden id="columNo" value="#{Z101TblSyokai.columNo}"/>
	</div>
	<!-- ########################### ボタンエリア ここまで ########################### -->

</div><!-- end of BODY_DIV -->
</h:form>
</div><!-- end of content -->
</div><!-- end of content-wrap -->

	<!-- ########################### フッタ ここから ########################### -->
    <div id="footer">
	  	<!-- メッセージエリア ここから -->
		<div id="sysmsg">
			<div id="MESSAGE_DIV" class="messageDiv" >
				<h:dataTable value="#{Z101TblSyokai.messages}" var="ses" border="0" styleClass="tblDetail" rowClasses="tblOddRow, tblEvenRow">
					<h:column><h:outputText value="#{ses.msg}" style="color:red;" /></h:column>
					<h:column><h:outputText value="#{ses.msgAction}" style="color:red;" /></h:column>
					<h:column><h:outputText value="#{ses.errorValue}" style="color:red;" /></h:column>
				</h:dataTable>
			</div>
        </div><!-- end of sysmsg -->
	  	<!-- メッセージエリア ここまで -->
		<!-- Fnキー設定　ここから -->
        <jsp:include page="../../Common/function_buttons.jsp" flush="true">
        	<jsp:param name="textf1" value="CSV" /><jsp:param name="statf1" value="enable" />
        	<jsp:param name="textf2" value="SQL" /><jsp:param name="statf2" value="enable" />
        	<jsp:param name="textf3" value="JAVA" /><jsp:param name="statf3" value="enable" />
        	<jsp:param name="textf4" value="検索" /><jsp:param name="statf4" value="enable" />
        	<jsp:param name="textf5" value="　" /><jsp:param name="statf5" value="enable" />
        	<jsp:param name="textf6" value="前頁" /><jsp:param name="statf6" value="enable" />
        	<jsp:param name="textf7" value="次頁" /><jsp:param name="statf7" value="enable" />
        	<jsp:param name="textf8" value="戻る" /><jsp:param name="statf8" value="enable" />
        	<jsp:param name="textf9" value="　" /><jsp:param name="statf9" value="enable" />
        	<jsp:param name="textf10" value="一覧CSV" /><jsp:param name="statf10" value="enable" />
        	<jsp:param name="textf11" value="修正" /><jsp:param name="statf11" value="enable" />
        	<jsp:param name="textf12" value="詳細" /><jsp:param name="statf12" value="enable" />
        </jsp:include>
		<!-- Fnキー設定　ここまで -->
		<h:inputHidden id="CLMLIST" value="#{Z101TblSyokai.clmList}"/>
		<h:inputHidden id="ROW_COUNT" value="#{Z101TblSyokaiList.rowCount}"/>
		<h:inputHidden id="PAGE_COUNT" value="#{Z101TblSyokai.pageCnt}"/>
		<h:inputHidden id="PAGE_HASMORE" value="#{Z101TblSyokai.hasMore}"/>
		
    </div><!-- end of footer -->
	<!-- ########################### フッタ ここまで ########################### -->
	
<!-- ########################### JavaSctipt設定 ここから ########################### -->
<script type="text/javascript"><!--
	
	var fid = document.forms[1].id;
	var clmlist = document.getElementById("CLMLIST").value;
	var row_count = document.getElementById("ROW_COUNT").value;
	var page_count = document.getElementById("PAGE_COUNT").value;
	var hasMore = document.getElementById("PAGE_HASMORE").value;

	//******************************************************************
	// 次へボタンクリック時の処理
	//******************************************************************
	function nextBtnClick() {
	}

	//******************************************************************
	// 初期化処理
	//******************************************************************
	function init() {
		
    	if ("W"==document.getElementById(fid+":toggle").value) {
    		//表示エリア拡大を、ページ推移時も有効にする
    		toggleSearchCondition('searchConditionArea');
    	}
    	
    	if (document.getElementById(fid + ":transition").value == "true") {
        	//遷移戻りの場合、再度検索を押下しリストを再取得する
    		document.getElementById(fid + ":transition").value="false";
        	f4();
    	} else {
        	//遷移戻りでは無い場合、画面を表示
  			 document.getElementById("ALL_DIV").style.display = "block";
    	}
    	
	}

    //******************************************************************
    //検索条件指定エリアの表示／非表示を切り替える。
    //******************************************************************
    function toggleSearchCondition(target){
      toggle(target);
      	
      //検索結果表示エリアの大きさを調整する。
      document.all.resultListArea.style.height = 
        (document.all.item(target).style.display == 'none' ? '460px' : '360px');
      //表示／非表示のアイコンを変更する。
      document.all.toggleImage.src = 
        (document.all.item(target).style.display == 'none' ? '../../img/triangle_right.gif' : '../../img/triangle_down.gif');
	  //表示エリアの拡大制御ON
      document.all.item(fid+":toggle").value = 
        (document.all.item(target).style.display == 'none' ? 'W' : 'N');
        
    }
    
    //******************************************************************
    //明細操作ボタン押下時の制御を行います
    //******************************************************************
	function MeisaiAction(kbn) {

		var btnName="";
		var rtn="";

		if (clmlist == 'true') {
			//カラムリストを表示している場合
			
			btnName="Select3";
			//詳細ボタン押下
			if (kbn=='2'){
				btnName="Select4";
			}

			// 明細の中でラジオボタンがチェックされている明細を取得してます
			// その明細内のbtnNameとして登録されている項目の名称を返却します
			rtn = getMeisaiName(document.forms[1].elements["selradio2"], fid, btnName, true, "list2");
		} else {
			//テーブルリストを表示している場合
			
			btnName="Select1";
			//詳細ボタン押下
			if (kbn=='2'){
				btnName="Select2";
			}

			// 明細の中でラジオボタンがチェックされている明細を取得してます
			// その明細内のbtnNameとして登録されている項目の名称を返却します
			rtn = getMeisaiName(document.forms[1].elements["selradio"], fid, btnName, true, "list");
		}

		if (rtn != "") {
			document.getElementById(fid + ":transition").value="true";
			document.getElementById(rtn).onclick();
		}
		
		displayDataSend_Recover();
		unblockAllFn();
    }

	// ファンクション有効／無効設定
	if (row_count == 0) {
		toggleFn("fn1","disabled");
	}
	if (row_count == 0) {
		toggleFn("fn2","disabled");
	}
	if (row_count == 0) {
		toggleFn("fn3","disabled");
	}
//	toggleFn("fn4","disabled");
	toggleFn("fn5","disabled");
	if (page_count == 0) {
		toggleFn("fn6", "disabled");
	}
	if (hasMore == 'false') {
		toggleFn("fn7", "disabled");
	}
//	toggleFn("fn8","disabled");
	toggleFn("fn9","disabled");
	if (row_count == 0) {
		toggleFn("fn10","disabled");
		toggleFn("fn11","disabled");
		toggleFn("fn12","disabled");
	}

	// ファンクション動作設定
	function f1() {
		if (setColumNo()) {
			document.getElementById(fid+":csvKbn").value="csv1";
	  		j$("#" + fid + "\\:csv").trigger("click");
			unblockAllFn();
		} else {
			unblockAllFn();
		}
	}
	function f2() {
		if (setColumNo()) {
			document.getElementById(fid+":csvKbn").value="csv3";
	  		j$("#" + fid + "\\:csv").trigger("click");
			unblockAllFn();
		} else {
			unblockAllFn();
		}
	}
	function f3() {
		if (setColumNo()) {
			document.getElementById(fid+":csvKbn").value="csv4";
	  		j$("#" + fid + "\\:csv").trigger("click");
			unblockAllFn();
		} else {
			unblockAllFn();
		}
	}
	function f4() {
		displayDataSend();
  		j$("#" + fid + "\\:searchButton").trigger("click");
	}
	function f5() {
	}
	function f6() {
		displayDataSend();	
  		j$("#" + fid + "\\:movePrev").trigger("click");
	}
	function f7() {
		displayDataSend();
  		j$("#" + fid + "\\:moveNext").trigger("click");
	}
	function f8() {
		displayDataSend();
  		j$("#" + fid + "\\:returnButton").trigger("click");
	}
	function f9() {}
	function f10() {
		document.getElementById(fid+":csvKbn").value="csv2";
  		j$("#" + fid + "\\:csv").trigger("click");
		unblockAllFn();
	}
	function f11() {
		displayDataSend();
  		j$("#" + fid + "\\:MeisaiButton1").trigger("click");
	}
	function f12() {
		displayDataSend();
  		j$("#" + fid + "\\:MeisaiButton2").trigger("click");
	}
	
	// リスト表示部をウィンドウのリサイズに合わせてリサイズさせる設定
	resizeList.add('searchResultHeader', 951, 'fixed');
	resizeList.add('resultListArea', 967, 360);

	//******************************************************************
	// 選択カラムＮＯのセット
	//******************************************************************
	function setColumNo() {
		// 明細の中でラジオボタンがチェックされている明細Noを取得してます
		if (clmlist == 'true') {
			var rtn = getMeisaiNo(document.forms[1].elements["selradio2"], fid, false);
		} else {
			var rtn = getMeisaiNo(document.forms[1].elements["selradio"], fid, false);
		}
		if (rtn<0) {
			alert("明細が選択されていません");
			return false;
		} else {
			// 選択されている明細Noが存在する場合、セットします
			document.getElementById(fid + ":columNo").value=rtn;
			return true;
		}
	}
--></script>
<!-- ########################### JavaSctipt設定 ここまで ########################### -->

</div><!-- end of wrap -->
</div><!-- end of all_div -->
</f:view>
</body>
</html>
