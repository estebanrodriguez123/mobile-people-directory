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
    'skype-plugin-people-directory',
    function (A) {
        Liferay.SkypePluginPeopleDirectory = {

            init: function (params) {
                var instance = this;
                
                instance.namespace = params.namespace;
                instance.container = params.container;
                
                instance.setComponents();
            },

            setComponents: function (container) {
                var instance = this;
                
                instance.messageError = instance.container.all(".portlet-msg-error");
                instance.alreadyInListMessage = instance.container.all(".alredy-in-list-msg");
                instance.skypeList = instance.container.one(".skype-users-to-call");
                instance.searchResults = instance.container.one("#searchResults");
                
                instance.SKYPE_TEMPLATES.skypeItem = A.one("#skype-item-template").get("innerHTML");
                
                instance.setSkypeUserListener(instance.CONSTANTS.SKYPE_CONTACT_TYPE);
                instance.setSkypeUserListener(instance.CONSTANTS.PHONE_CONTACT_TYPE);
                instance.setRemoveUserListener();                
                instance.setSkypeActionListener(instance.container.one("#"+instance.namespace+"skype-open"), instance.CONSTANTS.SKYPE_CHAT_ACTION);
                instance.setSkypeActionListener(instance.container.one("#"+instance.namespace+"skype-call"), instance.CONSTANTS.SKYPE_CALL_ACTION);
            },
            
            /** -------------------------------- LISTENERS ---------------------------------*/

            /**
            *   Adds the user of the currently clicked element to the list of
            *    users to call. Specific for skype icon
            */
            setSkypeUserListener: function(type) {
            	var instance = this;
            	
            	if (instance.searchResults) {
            		instance.searchResults.delegate("click", function(e){
            			instance.messageError.setStyle("display", "none");
            			instance.addUserToGroup({
            				type: type,
            				userId: e.currentTarget.getAttribute("userId"),
            				name: e.currentTarget.getAttribute("username"),
            				skypeId: e.currentTarget.getAttribute("title")
            			});
            			
            		}, ".icon-"+type);
            	}
            },
            
            setRemoveUserListener: function() {
                var instance = this;
                
                if (instance.skypeList) {
                	instance.skypeList.delegate("click", function(){
                		this.ancestor("li").remove();
                	}, ".handle");
                }
            },
            
            /**
            *   Opens Skype and loads the usernames/phone numbers if any has been added.
            */
            setSkypeActionListener: function(node, action) {
                var instance = this;
                
                if (node) {
                	node.on("click", function() { 
                		var items = instance.container.all("#users li");
                		
                		if (items.size() != 0) {
                			Skype.tryAnalyzeSkypeUri('chat', '0');
                			var users = instance.getCurrentSkypeUsers();
                			location.href = "skype:" + users + "?"+action;
                		}
                		else {
                			instance.messageError.setStyle("display", "block");
                		}
                	});
                }
            },
            
            /** -------------------------------- RENDERING FUNCTIONS ---------------------------------*/
            
            /**
            * Add users to the group list
            */
            addUserToGroup: function(data){
                var instance = this;
                
                if (!instance.isInList(data.skypeId)){
                    var template = A.Handlebars.compile(instance.SKYPE_TEMPLATES.skypeItem),
                    html = template(data);
                    instance.container.one("#users").append(html);
                    
                } else {
                     new A.Modal(
                      {
                        headerContent: instance.alreadyInListMessage.one(".header").get("outerHTML"),  
                        bodyContent: instance.alreadyInListMessage.one(".content").get("outerHTML"),
                        centered: true,                        
                        render: '#modal',
                        height: 120,
                        modal: true
                      }
                    ).render();
                }
            },
            
            /** -------------------------------- MISCELLANEOUS ---------------------------------*/

            /**
            *   Checks whether a user is already in the list of users to call
            */
            isInList: function(personId) {
                var instance = this, 
                	users = instance.getCurrentSkypeUsers();

                return users.indexOf(personId) != -1;
            },
            
            /**
            *   Returns a string with all selected skype users
            */
            getCurrentSkypeUsers: function() {
                var instance = this,
                	users = "";
                
                instance.container.all("#users li").each(function(li){
                    users += li.getAttribute("skypeId") + ";";
                });
                
                return users;
            },

            SKYPE_TEMPLATES: {
            	skypeItem: null
            },
            
            namespace: null,
            container: null,
            messageError: null,
            alreadyInListMessage: null,
            skypeList: null,
            searchResults: null,
            
            CONSTANTS: {
                SKYPE_CONTACT_TYPE: "skype",
                PHONE_CONTACT_TYPE: "phone",
                SKYPE_CALL_ACTION: "call",
                SKYPE_CHAT_ACTION: "chat"
            }
        };
    },
    '', {
        requires: ['node', 'event', 'event-key', 'aui-io-request', 'node-event-simulate', 'handlebars',
            'event-base', 'aui-modal'
        ]
    }
);