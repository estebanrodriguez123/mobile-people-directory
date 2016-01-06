
AUI.add('hangouts-plugin-people-directory', function(A) {
	Liferay.HangoutsPluginPeopleDirectory = {
						
			init: function (params) {
                var instance = this;
                
                instance.namespace = params.namespace;
                instance.container = params.container;
                instance.users = [];
                
                instance.setComponents();
            },
            
            setComponents: function() {
            	var instance = this;
            	
            	instance.userList = instance.container.one('#hangouts-users');
            	instance.buttonId = 'hangouts-button-placeholder';
            	
            	instance.alreadyInListMessage = instance.container.all(".alredy-in-list-msg");
            	instance.searchResults = instance.container.one("#searchResults");
            	
            	instance.hangoutsItem = A.one("#hangouts-item-template").get("innerHTML");
            	
            	instance.setSkypeUserListener();
            	instance.setRemoveUserListener();
            },
            
            addUser: function(email, username) {
            	var instance = this;
            	if(instance.users.indexOf(email) != -1) {
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
            	} else {
            		instance.users.push(email);
            		var template = A.Handlebars.compile(instance.hangoutsItem),
                    html = template({ username: username });
                    instance.userList.append(html);
            		instance.renderButton();
            	}
            },
            
            removeUser: function(email) {
            	var instance = this;
            	var index = instance.users.indexOf(email);
            	instance.users.splice(index, 1);
            	instance.renderButton();
            },
            
            renderButton: function() {
            	var instance = this;
            	if(instance.users.length > 0) {
            		var invites = [];
                	instance.users.forEach(function(email) {
                		invites.push({ id: email, invite_type: 'EMAIL' });
                	});
                	gapi.hangout.render(instance.buttonId, { 'render': 'createhangout', 'invites': invites });
            	} else {
            		A.one('#' + instance.buttonId).html('').setStyle('');
            	}
            },
            
            setSkypeUserListener: function() {
            	var instance = this;
            	
            	if (instance.searchResults) {
            		instance.searchResults.delegate("click", function(e){
            			instance.addUser(
            				e.currentTarget.getAttribute("userId"),
            				e.currentTarget.getAttribute("username")
            			);
            			
            		}, ".icon-google-plus");
            	}
            },
            
            setRemoveUserListener: function() {
                var instance = this;
                
                if (instance.userList) {
                	instance.userList.delegate("click", function(){
                		instance.removeUser(this.ancestor("li").attr("hangoutsId"));
                		this.ancestor("li").remove();
                	}, ".handle");
                }
            },
            
	};
}, '', { requires: ['node'] });