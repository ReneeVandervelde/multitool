# Resolve project root
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
export PROJECT_ROOT="$( cd -P "$( dirname "$SOURCE" )/../../../.." >/dev/null 2>&1 && pwd )"

##
# Shared Paths
##
PATH=$PATH:$PROJECT_ROOT/build/install/mt-system/bin
PATH=$PATH:$PROJECT_ROOT/src/main/bash/scripts
PATH=$PATH:./project-bin
PATH=$PATH:./bin
PATH=$PATH:./
