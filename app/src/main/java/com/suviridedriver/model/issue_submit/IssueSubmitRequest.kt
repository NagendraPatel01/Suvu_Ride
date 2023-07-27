package com.suviridedriver.model.issue_submit

data class IssueSubmitRequest(
    val subject: String,
    val emailId: String,
    val describeYourIssue: String
)