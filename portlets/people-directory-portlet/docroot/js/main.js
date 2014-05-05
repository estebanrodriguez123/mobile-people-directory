/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

AUI.add(
    'people-directory-plugin',
    function (A) {
        Liferay.PeopleDirectory = {

            init: function (params) {
                var instance = this;
                
                instance.portletId = params.portletId;
                instance.namespace = params.namespace;
                instance.container = params.container;
                instance.rowCount = params.rowCount;
                instance.fields = params.fields;
                
                instance.setComponents();
            },

            setComponents: function (container) {
                var instance = this;
                
                instance.searchInput = instance.container.one('#' + instance.namespace + 'keywords');
                instance.placeholderText = Liferay.Language.get("people-directory.label.type-keywords");
                
                instance.enablePlaceholder();
                
                instance.PEOPLE_DIRECTORY_TEMPLATES.searchResultsHeader = A.one('#search-result-header-template').get('innerHTML');
                instance.PEOPLE_DIRECTORY_TEMPLATES.contentSearchItem = A.one('#content-search-item-template').get('innerHTML');
                instance.PEOPLE_DIRECTORY_TEMPLATES.profileInfoTable = A.one('#profile-info-table-template').get('innerHTML');
                instance.PEOPLE_DIRECTORY_TEMPLATES.profileResult = A.one('#profile-result-template').get('innerHTML');
                
                instance.enableSearch();
                instance.addFieldNameAttribute();
            },
            
            enableSearch: function () {
            	var timeout;
                var instance = this;
                if ((themeDisplay.isSignedIn()) && (instance.searchInput != null)) {
                	instance.searchInput.on('keypress', function () {
                		if(timeout){
                			clearTimeout(timeout);
                		}
                		timeout = setTimeout(function (){
	                        var searchText = instance.searchInput.get('value');
	                        searchText = searchText.trim();
	                        if (searchText != null && searchText.length > 2 && searchText != instance.placeholderText) {
	                        	var maxItems = A.one('#' + instance.namespace + 'maxItems').get('value');
	                        		instance.performSearch(searchText, maxItems);
	                        }
                		}, 1000);
                	});
                }
            },
            
            enablePlaceholder: function() {
                var instance = this;
                
                if (instance.searchInput != null) {
                    
                    instance.searchInput.set('value', instance.placeholderText);
                    
                    instance.searchInput.on('focus', function() {
                        if (instance.searchInput.get('value') == instance.placeholderText) {
                            instance.searchInput.set('value', '');
                        }
                    });
                    
                    instance.searchInput.on('blur', function(){
                        if (instance.searchInput.get('value') == '') {
                            instance.searchInput.set('value', instance.placeholderText);
                        }
                    });
                }
            },

            performSearch: function (searchText, maxItems) {
                var instance = this;
                
                var pdAction = "keyword-search";
                var resourceURL = Liferay.PortletURL.createResourceURL();
    			resourceURL.setPortletId(instance.portletId);
    			resourceURL.setParameter("pdAction", pdAction);
    			resourceURL.setParameter("keywords", searchText);
    			resourceURL.setParameter("start", 0);
    			resourceURL.setParameter("end", maxItems);
    			
    			A.io(resourceURL.toString(), {
                    method: "GET",
                    dataType: 'json',
                    on: {
                        success: function (transactionid, response) {
                        	var responseData = A.JSON.parse(response.responseText);
                        	
                            instance.showSearchResults(responseData, pdAction);
                            
                            //creates paginator
                            if (!instance.paginator) {
                                instance.paginator = instance.createPaginator(responseData.searchCount);
                                instance.paginator.render();
                            } else {
                                instance.paginator.set('total', responseData.searchCount);
                                instance.paginator.set('page', 1);
                                instance.paginator.changeRequest();
                            }
                        },
                        failure: function () {
                            displayError("Error searching for keywords");
                        }
                    }
    			});

            },
            
            performCompleteProfileSearch: function (event) {
                var instance = this;
                event.halt();
                var item = event.currentTarget;
                var userId = item.attr('data-user-id');
                var fullName = item.attr('data-full-name');
                
                var resourceURL = Liferay.PortletURL.createResourceURL();
    			resourceURL.setPortletId(instance.portletId);
    			resourceURL.setParameter("pdAction", "show-complete-profile");
    			resourceURL.setParameter("userId", userId);
    			
    			A.io(resourceURL.toString(), {
                    method: "GET",
                    dataType: 'json',
                    on: {
                    	success: function (transactionid, response) {
                         	var responseData = A.JSON.parse(response.responseText);
                            responseData.id = userId;
                            responseData.fullName = fullName;
                            instance.showCompleteProfile(responseData);
                        },
                        failure: function (xhr, ajaxOptions, thrownError) {
                            displayError("Error searching for complete profile for userId" + userId);
                        }
                    } 
    			});
            },

            createPaginator: function (maxPagesLinks) {
                var instance = this;

                return new A.PaginatorOld({
                    alwaysVisible: false,
                    containers: '#paginator',
                    total: maxPagesLinks,
                    maxPageLinks: 15,
                    rowsPerPage: instance.rowCount,
                    nextPageLinkLabel: 'Next',
                    prevPageLinkLabel: 'Prev',
                    on: {
                        changeRequest: function (event) {
                            A.all('#searchResults .small-profile-box').setStyle('display', 'none');
                            var paginator = this;
                            var newState = event.state;
                            var page = newState.page;
                            if (instance.rowCount == 1) {
                                A.one('#searchResults .page' + page).setStyle('display', 'block');
                            } else {
                                var total = page * instance.rowCount;
                                var i = page == 1 ? total - instance.rowCount : total - instance.rowCount + 1;

                                for (i; i <= total; i++) {
                                    var item = A.one('#searchResults .page' + i);
                                    if (item != null)
                                        item.setStyle('display', 'block');
                                }
                            }
                            paginator.setState(newState);
                        }
                    },
                    template: '{FirstPageLink} {PrevPageLink} {PageLinks} {NextPageLink} {LastPageLink} {CurrentPageReport} {Total}'
                });
            },

            showSearchResults: function (responseData, pdAction) {
                var instance = this;
                var searchResults = "";
                if (responseData.resultsArray.length > 0) {
                	searchResultsText = A.Lang.sub(Liferay.Language.get("search-results-count"), {
                		0: responseData.searchCount,
                		1: (responseData.searchCount > 1 ? "s" : "")
                	});
                    searchResults += A.Lang.sub(instance.PEOPLE_DIRECTORY_TEMPLATES.searchResultsHeader, {
                        results: searchResultsText
                    });

                    if (pdAction == "keyword-search") {
                        for (var i = 0; i < responseData.resultsArray.length; i++) {
                            searchResults += instance.addSearchResult(responseData.resultsArray[i], i);
                        }
                    } else if (pdAction == "content-search") {

                        for (var i = 0; i < responseData.resultsArray.length; i++) {
                            searchResults += A.Lang.sub(instance.PEOPLE_DIRECTORY_TEMPLATES.contentSearchItem, {
                                title: responseData.resultsArray[i].title,
                                description: responseData.resultsArray[i].description,
                                username: responseData.resultsArray[i].userName
                            });
                        }
                    }
                } else {
                    searchResults += Liferay.Language.get("no-search-results-found");
                }
                if (A.UA.gecko > 0) {
                    A.one("#searchResults").html(searchResults);
                } else {
                    A.one("#searchResults").empty().append(searchResults);
                }
                // add handlers for the new elements
                A.all('div.slide-down').on('click', A.bind(instance.performCompleteProfileSearch, this));
                A.all('div.slide-down').on('click', A.bind(instance.slideDown, this));
                A.all('div.slide-up').on('click', A.bind(instance.slideUp, this));

            },

            showCompleteProfile: function (responseData) {
                var instance = this;
                var element = A.one("#" + responseData.id + "-small-profile-box .more-info");
                element.setContent("");
                var box = A.Handlebars.compile(instance.PEOPLE_DIRECTORY_TEMPLATES.profileInfoTable); 
                element.append(box(responseData));
            },

            addSearchResult: function (user, i) {
                var instance = this;
                user.itemNumber = (i + 1);
                
                var source = A.Handlebars.compile(instance.PEOPLE_DIRECTORY_TEMPLATES.profileResult);
                
                return source(user);
            },
            
            /** Expands user information */
            slideDown: function (event) {
                event.halt();
                /* to adjust size for some mobile devices 768 comes from liferay default mobile viewport breakpoints */
                var boxWidth = (A.one('body').get('winWidth') <= Liferay.PeopleDirectory.CONSTANTS.LIFERAY_PHONE_BREAKPOINT) ? Liferay.PeopleDirectory.CONSTANTS.SLIDE_DOWN_PICTURE_SIZE_PHONE : Liferay.PeopleDirectory.CONSTANTS.SLIDE_DOWN_PICTURE_SIZE,
                	item = event.currentTarget,
                	userId = item.attr('data-user-id'),
                	box = $("#" + userId + "-small-profile-box"),
                	image = box.find(".small-photo-box img"),
                	/*calculating image proportional height*/
                	boxHeight = image.height() * boxWidth.substring(0, boxWidth.length -2) / image.width();
                
                if (!boxHeight || boxHeight <= 0) boxHeight = boxWidth;
                
                box.find(".slide-down").hide();
                box.find(".more-info").show();
                box.find(".slide-up").show();
                
                image.height(boxHeight).width(boxWidth);
                
                box.find(".small-photo-box").animate({
                    height: boxHeight,
                    width: boxWidth
                }, "slow");
                
                box.find(".contact-short-info").hide();
            },

            slideUp: function (event) {
            	
                event.halt();
                
                var item = event.currentTarget,
                	userId = item.attr('data-user-id'),
                	box = $("#" + userId + "-small-profile-box"),
                	image = box.find(".small-photo-box img"),
                	boxWidth = Liferay.PeopleDirectory.CONSTANTS.PICTURE_SIZE,
                	/*calculating image proportional height*/
                	boxHeight = image.height() * boxWidth.substring(0, boxWidth.length -2) / image.width();
                
                if (!boxHeight || boxHeight <= 0) boxHeight = boxWidth;
                
                box.find(".slide-down").show();
                box.find(".more-info").hide();
                box.find(".slide-up").hide();
                
                image.animate({
                	height: boxHeight,
                    width: boxWidth
                }, "slow");
                box.find(".small-photo-box").animate({
                    height: boxHeight,
                    width: boxWidth
                }, "slow");
                
                box.find(".contact-short-info").show();
                
            },
            
            /*For mobile devices adding header name in the same row of the value field
             * using CSS3 content rule  */
            addFieldNameAttribute: function() {
            	var instance = this;
            	var rows =  instance.container.all(".searchcontainer .table-data tr");
            	rows.each(function(row){
            		row.all("td").each(function(column, index){
            			column.setAttribute("cell-data", Liferay.Language.get(instance.fields[index]));
            		});
            		
            	});
            },

            PEOPLE_DIRECTORY_TEMPLATES: {
                searchResultsHeader: null,
                contentSearchItem: null,
                profileInfoTable: null,
                profileResult: null
            },
            
            portletId: null,
            namespace: null,
            container: null,
            paginator: null,
            rowCount: null,
            searchInput: null,
            placeholderText: null,
            fields: null,
            
            CONSTANTS: {
                LIFERAY_PHONE_BREAKPOINT: 768, // phone media query breakpoint defined by liferay
                PICTURE_SIZE: '55px', // picture size, width and height
                SLIDE_DOWN_PICTURE_SIZE_PHONE: '80px', // image size when user is expanded
                SLIDE_DOWN_PICTURE_SIZE: '130px' // image size when user is expanded
            }
        };
    },
    '', {
        requires: ['node', 'event', 'event-key', 'aui-io-request', 'node-event-simulate', 'handlebars',
            'event-base', 'aui-paginator-old', 'aui-form-validator', 'liferay-portlet-url', 'json-parse',
            'jquery'
        ]
    }
);