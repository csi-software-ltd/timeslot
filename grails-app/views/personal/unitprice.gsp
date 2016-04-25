<g:csilabel for="unitprice_price">Цена:</g:csilabel>
<input type="text" class="auto" id="unitprice_price" name="price" value="${number(value:unitprice?.price)}"/>
<br/><g:csilabel for="unitprice_forperiod">Размерность:</g:csilabel>
<input type="text" class="auto" id="unitprice_forperiod" name="forperiod" value="${unitprice?.forperiod}"/>
<g:csilabel for="unitprice_minperiod">Мин. резерв:</g:csilabel>
<input type="text" class="auto" id="unitprice_minperiod" name="minperiod" value="${unitprice?.minperiod}"/>
<g:if test="${is_exception}">
	<g:csilabel for="unitprice_days" style="height:60px;float:left">Применяется для:</g:csilabel>
	<div id="unitprice_days" class="fright" style="width:746px">
		<label class="auto" for="unitprice_is_mon">
			<input type="checkbox" id="unitprice_is_mon" name="is_mon" value="1" <g:if test="${unitprice?.is_mon}">checked</g:if> />
			Понедельник
		</label>
		<label class="auto" for="unitprice_is_tue">
			<input type="checkbox" id="unitprice_is_tue" name="is_tue" value="1" <g:if test="${unitprice?.is_tue}">checked</g:if> />
			Вторник
		</label>
		<label class="auto" for="unitprice_is_wen">
			<input type="checkbox" id="unitprice_is_wen" name="is_wen" value="1" <g:if test="${unitprice?.is_wen}">checked</g:if> />
			Среда
		</label>
		<label class="auto" for="unitprice_is_thu">
			<input type="checkbox" id="unitprice_is_thu" name="is_thu" value="1" <g:if test="${unitprice?.is_thu}">checked</g:if> />
			Четверг
		</label>
		<label class="auto" for="unitprice_is_fra">
			<input type="checkbox" id="unitprice_is_fra" name="is_fra" value="1" <g:if test="${unitprice?.is_fra}">checked</g:if> />
			Пятница
		</label>
		<label class="auto" for="unitprice_is_sut">
			<input type="checkbox" id="unitprice_is_sut" name="is_sut" value="1" <g:if test="${unitprice?.is_sut}">checked</g:if> />
			Суббота
		</label>
		<label class="auto" for="unitprice_is_sun">
			<input type="checkbox" id="unitprice_is_sun" name="is_sun" value="1" <g:if test="${unitprice?.is_sun}">checked</g:if> />
			Воскресение
		</label>
		<label class="auto" for="unitprice_is_hol">
			<input type="checkbox" id="unitprice_is_hol" name="is_hol" value="1" <g:if test="${unitprice?.is_hol}">checked</g:if> />
			Праздники
		</label>
	</div>
	<div class="clear"></div>
	<g:csilabel for="unitprice_time_start">Время с:</g:csilabel>
	<g:timepicker class="normal nopad" style="margin-right:68px" name="unitprice_time_start" value="${unitprice?String.format('%tH:%<tM',unitprice?.time_start):''}" interval="${interval}" />
	<g:csilabel for="unitprice_time_end">Время по:</g:csilabel>
	<g:timepicker class="normal nopad" name="unitprice_time_end" value="${unitprice?String.format('%tH:%<tM',unitprice?.time_end):''}" interval="${interval}" />
	<div class="clear" style="padding-bottom:10px"></div>
</g:if>
<div class="fright">
  <input type="submit" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#unitpriceAddForm').slideUp();"/>
</div>
<input type="hidden" name="unit_id" value="${unit.id}"/>
<input type="hidden" name="id" value="${unitprice?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>