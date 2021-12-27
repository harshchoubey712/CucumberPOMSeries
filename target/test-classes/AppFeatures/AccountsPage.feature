Feature: Account Page Feature

Background:
Given user has already logged in to application
|username|password|
|dec2020secondbatch@gmail.com|Selenium@12345|

#We are using the concept of datatables in background .
#We are not using concept of examples using data driven.

@accounts
Scenario: Accounts page title
Given user is on Accounts page
When user gets the title of the page
Then page title should be "My account - My Store"

@accounts
Scenario: Accounts section count
Given user is on Accounts page
Then user gets accounts section
#Here also we are using datatable concept using user gets account section details.
|ORDER HISTORY AND DETAILS|
|MY CREDIT SLIPS|
|MY ADDRESSES|
|MY PERSONAL INFORMATION|
|MY WISHLISTS|
|Home|
And accounts section count should be 6

