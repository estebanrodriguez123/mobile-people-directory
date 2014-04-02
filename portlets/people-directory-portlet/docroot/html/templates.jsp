<script id="search-result-header-template" type="text/x-template">
   <div class="results">{results}</div>
</script>
<script id="content-search-item-template" type="text/x-template">
   <div class="business-card">
       <div class="document-title">{title}</div>
       <div class="document-url">{description}</div>
       <div class="full-name">{username}</div>
   </div>
</script>
<script id="profile-info-table-template" type="text/x-template">
   <table class="profile-info-table">
       <tr><td class="field-value more-info-title"><liferay-ui:message key="people-directory.label.job-title" />:</td><td class="info">{jobTitle}</td></tr>
       <tr><td class="field-value more-info-title"><liferay-ui:message key="people-directory.label.screen-name" />:</td><td class="info" >{screenName}</td></tr>
       <tr><td class="field-value more-info-title"><liferay-ui:message key="people-directory.label.city" />:</td><td class="info" >{city}</td></tr>
       <tr><td class="field-value more-info-title"><liferay-ui:message key="people-directory.label.phone" />:</td><td class="info">{phone}</td></tr>
   </table>
</script>
<script id="profile-result-template" type="text/x-template">
   <div class="small-profile-box page{itemNumber}" id ="{id}-small-profile-box">
       <div class="small-photo-box" id="{id}-small-photo-box">
       <img src="{portraitUrl}" height="55" id="{id}-picture" />
       </div>
       <div class="summary-box" id ="{id}-summary-box">
       <div class="field-value">{fullName}</div>
       <div class="mail field-value"><a href="mailto:{emailAddress}">{emailAddress}</a></div>
       <div class="more-info" style="display:none" id="{id}-more-info"></div>
       </div>
       <div class="slide-down" data-user-id="{id}" id="{id}-slide-down" ></div>
       <div class="slide-up" data-user-id="{id}" id="{id}-slide-up" style="display:none"></div>
       <div class="clearfix"></div>
   </div>
</script>