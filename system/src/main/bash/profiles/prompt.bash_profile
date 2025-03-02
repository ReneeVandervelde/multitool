##
# Prompt
##
__prompt_command() {
    local EXIT="$?"
    local EXIT_FORMAT=$(printf "%03d" "$EXIT")
    PS1=""

    local ResetColor='\[\e[0m\]'
    local Grey='\[\e[01;30m\]'
    local Red='\[\e[01;31m\]'
    local Yellow='\[\e[01;33m\]'
    local Green='\[\e[01;32m\]'

    if [ $EXIT == 50 ]; then
        PS1+="${Red}${EXIT_FORMAT}${ResetColor}";
    elif [ $EXIT == 40 ] || [ $EXIT == 41 ]; then
        PS1+="${Yellow}${EXIT_FORMAT}${ResetColor}";
    elif [ $EXIT != 0 ]; then
        PS1+="${Grey}${EXIT_FORMAT}${ResetColor}";
    else
        PS1+="•••";
    fi

    if git rev-parse --is-inside-work-tree &>/dev/null; then
        if git diff --quiet 2>/dev/null >&2; then
            PS1+="${Green}•${ResetColor}"
        else
            PS1+="${Yellow}•${ResetColor}"
        fi
    else
        PS1+="•"
    fi

    PS1+=" \$ "
}

PROMPT_COMMAND=__prompt_command
