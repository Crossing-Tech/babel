#!/bin/bash

branch=$( git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/ (\1)/' | tr -d '()' )

if [[ $branch != "master" ]]
then

    if ! git diff-files --quiet --ignore-submodules --ignore-space-at-eol --
    then
        echo >&2 "cannot build: you have unstaged changes."
        exit -1
    fi

    # Disallow uncommitted changes in the index
    if ! git diff-index --cached --quiet HEAD --ignore-submodules --ignore-space-at-eol --
    then
        echo >&2 "cannot build: your index contains uncommitted changes."
        exit -1
    fi

fi
