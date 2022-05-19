# How to contribute

We really glad you're reading this, because we are eager to meet new contributors to the project.

Here are some important resources:

* [Minigames Bot support server](http://discord.gg/u2f6N7mvek) has plenty of stuff about the bot,

## Testing

Host a bot with the code on your machine and test it before creating the pull request. Look for possible bugs before saying it's ready.

## Submitting changes

Please send a [GitHub Pull Request to minigames](https://github.com/adex720/Minigames/pull/new/master) with a clear list of what you've done (read more about [pull requests](http://help.github.com/pull-requests/)). When you send a pull request, we will love you forever if you include RSpec examples. We can always use more test coverage. Please follow our coding conventions (below) and make sure all of your commits are atomic (one feature per commit).

Always write a clear log message for your commits. One-line messages are fine for small changes, but bigger changes should look like this:

    $ git commit -m "A brief summary of the commit
    > 
    > A paragraph describing what changed and its impact."

## Coding conventions

Start reading our code and you'll get the hang of it. We optimize for readability:

* We indent using four spaces (tabs)
* We use JSON for saving data
* We use JSON to load values that might chance on future (ex. Discord emote ids)
* We ALWAYS put spaces after list items and method parameters (`[1, 2, 3]`, not `[1,2,3]`), around operators (`x += 1`, not `x+=1`), and before opening curly brackets
* We put opening curly brackets on the same line
* We include curly brackets if the next statement is on the next line
* We use comments to explain everything that isn't obvious at the first glance

Thanks,
ADEX720, creator of the bot