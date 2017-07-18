var gulp = require('gulp');

var bower = require('gulp-bower');
var gutil = require('gulp-util');
var jshint = require('gulp-jshint');
var cleanCSS = require('gulp-clean-css');
var concat = require('gulp-concat');
var rename = require("gulp-rename");
var uglify = require('gulp-uglify');

var config = {
    accessKeyId: "AKIAID24IU7XN4ECH6WQ",
    secretAccessKey: "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"
};

var s3 = require('gulp-s3-upload')(config);

var src_dir = 'src/main/webapp';

var publishdir = 'assets';
var dist = {
  dir: publishdir,
  css:  publishdir+'/css/',
  js: publishdir+'/js/',
  vendor: publishdir+'/vendor/'
};

// Bower install packages
gulp.task('bower', function() {
  return bower();
});

// Lint Task
gulp.task('lint', function() {
    return gulp.src(src_dir+'/js/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

// Minify CSS
gulp.task('minify-css', function() {
    return gulp.src(src_dir+'/css/*.css')
        .pipe(concat('facilio.css'))
        .pipe(gulp.dest(dist.css))
        .pipe(cleanCSS({ compatibility: 'ie8' }))
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest(dist.css))
});

// Minify JS
gulp.task('minify-js', function() {
    return gulp.src(src_dir+'/js/*.js')
        .pipe(concat('facilio.js'))
        .pipe(gulp.dest(dist.js))
        .pipe(uglify())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest(dist.js))
});

// Copy vendor libraries from /bower_components into /vendor
gulp.task('copy', function() {
    gulp.src(['bower_components/bootstrap/dist/**/*', '!**/npm.js', '!**/bootstrap-theme.*', '!**/*.map'])
        .pipe(gulp.dest(dist.vendor+'/bootstrap'))

    gulp.src(['bower_components/bootstrap-social/*.css', 'bower_components/bootstrap-social/*.less', 'bower_components/bootstrap-social/*.scss'])
        .pipe(gulp.dest(dist.vendor+'/bootstrap-social'))

    gulp.src(['bower_components/datatables/media/**/*'])
        .pipe(gulp.dest(dist.vendor+'/datatables'))

    gulp.src(['bower_components/datatables-plugins/integration/bootstrap/3/*'])
        .pipe(gulp.dest(dist.vendor+'/datatables-plugins'))

    gulp.src(['bower_components/datatables-responsive/css/*', 'bower_components/datatables-responsive/js/*'])
        .pipe(gulp.dest(dist.vendor+'/datatables-responsive'))

    gulp.src(['bower_components/flot/*.js'])
        .pipe(gulp.dest(dist.vendor+'/flot'))

    gulp.src(['bower_components/flot.tooltip/js/*.js'])
        .pipe(gulp.dest(dist.vendor+'/flot-tooltip'))

    gulp.src(['bower_components/font-awesome/**/*', '!bower_components/font-awesome/*.json', '!bower_components/font-awesome/.*'])
        .pipe(gulp.dest(dist.vendor+'/font-awesome'))

    gulp.src(['bower_components/jquery/dist/jquery.js', 'bower_components/jquery/dist/jquery.min.js'])
        .pipe(gulp.dest(dist.vendor+'/jquery'))

    gulp.src(['bower_components/metisMenu/dist/*'])
        .pipe(gulp.dest(dist.vendor+'/metisMenu'))

    gulp.src(['bower_components/morrisjs/*.js', 'bower_components/morrisjs/*.css', '!bower_components/morrisjs/Gruntfile.js'])
        .pipe(gulp.dest(dist.vendor+'/morrisjs'))

    gulp.src(['bower_components/raphael/raphael.js', 'bower_components/raphael/raphael.min.js'])
        .pipe(gulp.dest(dist.vendor+'/raphael'))

    gulp.src(['bower_components/amazon-cognito-identity-js/dist/*'])
        .pipe(gulp.dest(dist.vendor+'/amazon-cognito-identity-js'))

    gulp.src(['bower_components/moment/min/*'])
        .pipe(gulp.dest(dist.vendor+'/moment'))

    gulp.src(['bower_components/fullcalendar/dist/*'])
        .pipe(gulp.dest(dist.vendor+'/fullcalendar'))
        
    gulp.src(['bower_components/fullcalendar-scheduler/dist/*'])
        .pipe(gulp.dest(dist.vendor+'/fullcalendar-scheduler'))

    gulp.src(['bower_components/d3/*'])
        .pipe(gulp.dest(dist.vendor+'/d3'))
})

gulp.task("upload", function() {
    gulp.src(dist.dir+'/**')
        .pipe(s3({
            Bucket: 'facilio-static', //  Required 
            ACL:    'public-read'       //  Needs to be user-defined 
        }, {
            // S3 Constructor Options, ie: 
            maxRetries: 5
        }))
    ;
});

// Run everything
gulp.task('default', ['bower', 'lint', 'minify-css', 'minify-js', 'copy', 'upload']);

// Dev task
gulp.task('dev', ['bower', 'lint', 'minify-css', 'minify-js'], function() {
    gulp.watch(src_dir+'/css/*.css', ['minify-css']);
    gulp.watch(src_dir+'/js/*.js', ['minify-js']);
});