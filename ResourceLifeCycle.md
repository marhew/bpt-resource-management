# Roles #

We distinguish two roles, **resource providers** and **moderators**. The former provide and manage content to the platform, whereas a provider may manage several resources. Moderators have the task to approve or deny requests, and have full control over what can be seen online.

In any case, a **moderator** is always also a **provider**, that is, they can upload their own resources and control their visibility without the need to re-login as another role.

# Life Cycle #

[![](https://docs.google.com/drawings/pub?id=11s59QdnkOflaT443zNGYQKYhGEiaK2zQhghjFViwzbc&w=960&h=720&nonsense.png)](https://docs.google.com/drawings/d/11s59QdnkOflaT443zNGYQKYhGEiaK2zQhghjFViwzbc/edit)

Source:

# Actions #
'**' also applies to bpmai**

| **provider** | **moderator** | **system** | **trigger** |
|:-------------|:--------------|:-----------|:------------|
| create`*`    |               |            | notify moderator, request to publish or reject |
|              | publish`*`    |            | notify provider |
|              | reject`*`     |            | notify provider |
|              | propose`*`    |            | notify provider |
| update`*`    |               |            | notify moderator, request to approve (do nothing) or reject |
| unpublish`*` |               |            | notify moderator |
|              | unpublish`*`  |            | notify provider |
| delete`*`    |               |            | notify moderator |
|              | delete`*`(only bpmai) |            | notify provider -- or do nothing |
|              |               | check url  | notify moderator and provider if issues exist |
|              |               | check dates | notify moderator and provider if model will become outdated soon |
|              |               | check dates | unpublish model, notify moderator and provider of timeout |