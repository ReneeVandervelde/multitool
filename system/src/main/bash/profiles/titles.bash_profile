function set-title() {
    export __TITLE_OVERRIDE=$@
}

function clear-title() {
    unset __TITLE_OVERRIDE
}

function __setTitle() {
    echo -ne "\033]0;$@\007"
}

function __setDefaultTitle() {
    if [ -z ${__TITLE_OVERRIDE+x} ]; then
        case "$BASH_COMMAND" in
            "__prompt_command")
                __setTitle $(hostname) - $(basename $PWD)
                ;;
            *)
                __setTitle ${BASH_COMMAND%% *}
                ;;
        esac
    else
        __setTitle $__TITLE_OVERRIDE
    fi
}

trap __setDefaultTitle DEBUG
