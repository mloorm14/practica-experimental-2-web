<?php
declare(strict_types=1);
session_start();

require_once 'includes/auth.php';

if (isLoggedIn()) {
    header('Location: dashboard.php');
} else {
    header('Location: login.php');
}
exit;
