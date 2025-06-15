#!/bin/bash
set -e

# Default UID and GID if not provided
USER_ID=${PUID:-1000}
GROUP_ID=${PGID:-1000}
USERNAME=${USERNAME:-user}

# Create group and user with provided UID/GID
groupadd -g $GROUP_ID $USERNAME || true
useradd -m -u $USER_ID -g $GROUP_ID -s /bin/bash $USERNAME || true

# Change ownership of home directory (just in case)
chown -Rv $USER_ID:$GROUP_ID /home/$USERNAME
chown -Rv $USER_ID:$GROUP_ID /config
chown -Rv $USER_ID:$GROUP_ID /app

# Execute the container command as the created user
exec gosu $USERNAME "$@"