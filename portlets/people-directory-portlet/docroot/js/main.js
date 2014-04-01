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

                instance.namespace = params.namespace;
                instance.resourceURL = params.resourceURL;
                instance.container = params.container;
                instance.rowCount = params.rowCount;

                instance.setComponents();
            },

            setComponents: function (container) {
                var instance = this;
                instance.enableSearch();
            },

            enableSearch: function () {
                var instance = this;

                if (themeDisplay.isSignedIn()) {
                    if (A.one('#' + instance.namespace + 'keywords') != null) {
                        A.one('#' + instance.namespace + 'keywords').on('keyup', function () {
                            var searchText = A.one('#' + instance.namespace + 'keywords').get('value');

                            // Disabling "Search By Content" for now           
                            var searchContent = false;

                            var maxItems = A.one('#' + instance.namespace + 'maxItems').get('value');
                            if (searchText != null && searchText.length > 2) {
                                instance.performSearch(searchText, searchContent, maxItems);
                            }
                        });
                    }
                }
            },

            performSearch: function (searchText, searchContent, maxItems) {
                var instance = this;
                var url = instance.resourceURL;
                var pdAction = searchContent == 'true' ? "content-search" : "keyword-search";
                A.io.request(url, {
                    method: "GET",
                    data: {
                        "pdAction": pdAction,
                        "keywords": searchText,
                        "start": 0,
                        "end": maxItems
                    },
                    dataType: 'json',
                    on: {
                        success: function () {
                            var responseData = this.get('responseData');
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
                        failure: function (xhr, ajaxOptions, thrownError) {
                            displayError("Error searching for keywords");
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
                    searchResults += A.Lang.sub(instance.PEOPLE_DIRECTORY_TEMPLATES.searchResultsHeader, {
                        total: responseData.searchCount,
                        pluralization: (responseData.searchCount > 1 ? "s" : "")
                    });

                    if (pdAction == "keyword-search") {
                        //alert("showSearchResults, results array length: " + responseData.resultsArray.length);
                        for (var i = 0; i < responseData.resultsArray.length; i++) {
                            //alert(responseData.resultsArray[i].fullName);
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
                    searchResults += "<div>No Search Results Found</div>";
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

            performCompleteProfileSearch: function (event) {
                var instance = this;
                event.halt();
                var url = instance.resourceURL;
                var item = event.currentTarget;
                var userId = item.attr('data-user-id');
                A.io.request(url, {
                    method: "GET",
                    data: {
                        "pdAction": 'show-complete-profile',
                        "userId": userId
                    },
                    dataType: 'json',
                    on: {
                        success: function () {
                            instance.showCompleteProfile(this.get('responseData'), userId);
                        },
                        failure: function (xhr, ajaxOptions, thrownError) {
                            displayError("Error searching for complete profile for userId" + userId);
                        }
                    }
                });
            },

            showCompleteProfile: function (responseData, userId) {
                var instance = this;
                var element = A.one("#" + userId + "-more-info");
                element.setContent("");
                var box = A.Lang.sub(instance.PEOPLE_DIRECTORY_TEMPLATES.profileInfoTable, responseData);
                element.append(box);
            },

            addSearchResult: function (user, i) {
                var instance = this;
                user.itemNumber = (i + 1);
                var box = A.Lang.sub(instance.PEOPLE_DIRECTORY_TEMPLATES.profileResult, user);

                return box;
            },

            slideDown: function (event) {
                /* to adjust size for some mobile devices 768 comes from liferay default mobile viewport breakpoints */
                var boxSize = (A.one('body').get('winWidth') <= 768) ? '80px' : '130px';
                event.halt();
                var item = event.currentTarget;
                var user_id = item.attr('data-user-id');
                $("#" + user_id + "-slide-down").hide();
                //$("#" + user_id + "-small-profile-box").animate( {height: boxSize}, "slow");
                $("#" + user_id + "-more-info").show();
                $("#" + user_id + "-slide-up").show();
                //$("#" + user_id + "-picture").width("120px");
                $("#" + user_id + "-picture").height(boxSize).width(boxSize);
                $("#" + user_id + "-small-photo-box").animate({
                    height: boxSize,
                    width: boxSize
                }, "slow");
            },

            slideUp: function (event) {
                event.halt();
                var item = event.currentTarget;
                var user_id = item.attr('data-user-id');
                $("#" + user_id + "-slide-up").hide();
                //$("#" + user_id + "-small-profile-box").animate( {height: "55px"}, "slow");	
                $("#" + user_id + "-more-info").hide();
                $("#" + user_id + "-slide-down").show();
                //$("#" + user_id + "-picture").width("60px");
                $("#" + user_id + "-picture").animate({
                    height: "55px",
                    width: "55px"
                }, "slow");
                $("#" + user_id + "-small-photo-box").animate({
                    height: "55px",
                    width: "55px"
                }, "slow");
            },

            PEOPLE_DIRECTORY_TEMPLATES: {
                searchResultsHeader: '<div class="results">Total: {total} user{pluralization} found</div>',
                contentSearchItem: '<div class="business-card">' +
                    '<div class="document-title">{title}</div>' +
                    '<div class="document-url">{description}</div>' +
                    '<div class="full-name">{username}</div>' +
                    '</div>',
                profileInfoTable: '<table border="0" cellpadding="0" cellspacing="0" width="100%">' +
                    '<tr><td>Job Title:</td><td class="info">{jobTitle}</td></tr>' +
                    '<tr><td>Screen Name:</td><td class="info" >{screenName}</td></tr>' +
                    '<tr><td>City:</td><td class="info" >{city}</td></tr>' +
                    '<tr><td>Phone:</td><td class="info">{phone}</td></tr>' +
                    '</table>',
                profileResult: '<div class="small-profile-box page{itemNumber}" id ="{id}-small-profile-box">' +
                    '<div class="small-photo-box" id="{id}-small-photo-box">' +
                    '<img src="{portraitUrl}" height="55" id="{id}-picture" />' +
                    '</div>' +
                    '<div class="summary-box" id ="{id}-summary-box">' +
                    '<div class="field-value">{fullName}</div>' +
                    '<div class="mail field-value"><a href="mailto:{emailAddress}">{emailAddress}</a></div>' +
                    '<div class="more-info" style="display:none" id="{id}-more-info"></div>' +
                    '</div>' +
                    '<div class="slide-down" data-user-id="{id}" id="{id}-slide-down" ></div>' +
                    '<div class="slide-up" data-user-id="{id}" id="{id}-slide-up" style="display:none"></div>' +
                    '<div class="clearfix"></div>' +
                    '</div>'
            },

            container: null,
            paginator: null,
            rowCount: null

        };
    },
    '', {
        requires: ['node', 'event', 'event-key', 'aui-io-request', 'node-event-simulate',
            'event-base', 'aui-paginator-old', 'aui-form-validator'
        ]
    }
);