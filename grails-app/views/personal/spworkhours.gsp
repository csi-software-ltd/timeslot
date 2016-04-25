<g:csilabel for="workhours_days" style="height:60px;float:left">Часы работы:</g:csilabel>
<div id="workhours_days" class="fright" style="width:746px">
	<label class="auto" for="workhours_is_mon">
		<input type="checkbox" id="workhours_is_mon" name="is_mon" value="1" <g:if test="${workhours?.is_mon}">checked</g:if> />
		Понедельник
	</label>
	<label class="auto" for="workhours_is_tue">
		<input type="checkbox" id="workhours_is_tue" name="is_tue" value="1" <g:if test="${workhours?.is_tue}">checked</g:if> />
		Вторник
	</label>
	<label class="auto" for="workhours_is_wen">
		<input type="checkbox" id="workhours_is_wen" name="is_wen" value="1" <g:if test="${workhours?.is_wen}">checked</g:if> />
		Среда
	</label>
	<label class="auto" for="workhours_is_thu">
		<input type="checkbox" id="workhours_is_thu" name="is_thu" value="1" <g:if test="${workhours?.is_thu}">checked</g:if> />
		Четверг
	</label>
	<label class="auto" for="workhours_is_fra">
		<input type="checkbox" id="workhours_is_fra" name="is_fra" value="1" <g:if test="${workhours?.is_fra}">checked</g:if> />
		Пятница
	</label>
	<label class="auto" for="workhours_is_sut">
		<input type="checkbox" id="workhours_is_sut" name="is_sut" value="1" <g:if test="${workhours?.is_sut}">checked</g:if> />
		Суббота
	</label>
	<label class="auto" for="workhours_is_sun">
		<input type="checkbox" id="workhours_is_sun" name="is_sun" value="1" <g:if test="${workhours?.is_sun}">checked</g:if> />
		Воскресение
	</label>
	<label class="auto" for="workhours_is_hol">
		<input type="checkbox" id="workhours_is_hol" name="is_hol" value="1" <g:if test="${workhours?.is_hol}">checked</g:if> />
		Праздники
	</label>
</div>
<div class="clear"></div>
<g:csilabel for="workhours_time_open">Время с:</g:csilabel>
<g:timepicker class="normal nopad" style="margin-right:108px" name="workhours_time_open" value="${workhours?String.format('%tH:%<tM',workhours?.time_open):'09:00'}" interval="${interval}" />
<g:csilabel for="workhours_time_close">Время по:</g:csilabel>
<g:timepicker class="normal nopad" name="workhours_time_close" value="${workhours?String.format('%tH:%<tM',workhours?.time_close):'18:00'}" interval="${interval}" />
<g:csilabel for="workhours_date_start">Срок действия с:</g:csilabel>
<g:datepicker class="normal nopad" style="margin-right:108px" name="workhours_date_start" value="${String.format('%td.%<tm.%<tY',workhours?.date_start?:new Date())}" />
<g:csilabel for="workhours_date_end">Срок действия по:</g:csilabel>
<g:datepicker class="normal nopad" name="workhours_date_end" value="${String.format('%td.%<tm.%<tY',workhours?.date_end?:new Date()+365)}" />
<div class="clear" style="padding-bottom:10px"></div>
<div class="fright">
  <input type="submit" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#spworkhoursAddForm').slideUp();"/>
</div>
<input type="hidden" name="space_id" value="${space.id}"/>
<input type="hidden" name="id" value="${workhours?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>