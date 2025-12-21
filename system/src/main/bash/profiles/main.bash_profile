# Resolve project root
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
export MT_PROJECT_ROOT="$( cd -P "$( dirname "$SOURCE" )/../../../../.." >/dev/null 2>&1 && pwd )"

# Load in profile sources:
for f in $MT_PROJECT_ROOT/system/src/main/bash/profiles/*.bash_profile; do
    if [[ "$f" == "$MT_PROJECT_ROOT/system/src/main/bash/profiles/main.bash_profile" ]]; then continue; fi
    source $f
done

# Load local override last
if [ -f $HOME/.local.bash_profile ]; then
  source $HOME/.local.bash_profile
fi
