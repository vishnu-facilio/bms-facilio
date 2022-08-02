echo "started"
git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
previousdaycommit=`git rev-list -1 --before="24 hour ago" master`
git fetch --all
git branch
echo $previousdaycommit  
git checkout production
git merge $previousdaycommit
git push -v --tags origin production
echo "success"       
git checkout master 
git branch           
currentdaycommit=`git rev-list -1 --since="24 hour" master`
echo $currentdaycommit
inbetweencommit=`git rev-list --all $previousdaycommit..$currentdaycommit master --not production` 
echo $inbetweencommit
for allcommits in $inbetweencommit;
 do
  revertcommit=` git rev-list --all  $allcommits --grep="Revert" $previousdaycommit..$currentdaycommit master --not production`; 
 done
echo $revertcommit
if [[ $revertcommit ]]
then
 for actualfromrevertcommit in $revertcommit;
   do   
    revertoriginalcommit=` git log $actualfromrevertcommit  $previousdaycommit..$currentdaycommit --format=%B | grep -oP '(?<=This reverts commit )[^ ]*' | tr -d '.'`;          
   done;
echo $revertoriginalcommit
 for matchcommit in $revertoriginalcommit
 do
 for matchinbetweencommit in $inbetweencommit
 do  
  if [[  " $matchcommit " =~ " $matchinbetweencommit " ]] 
  then
    echo  "$matchcommit commit present in the last 24 hours ";
    removedactualcommit="${revertoriginalcommit[@]/$matchcommit/} "${arr[@]/%+([[:blank:]])/}""; 
    break;
  fi;
 done;
 revertoriginalcommit="$removedactualcommit";
 done;  
for seperaterevertcommit in $revertcommit 
do
comparewithoriginal=` git rev-list -1 $seperaterevertcommit --format=%B | grep -oP '(?<=This reverts commit )[^ ]*' | tr -d '.' `;
for revokematchcommit in $removedactualcommit
do 
if [[ " $comparewithoriginal " =~ " $revokematchcommit " ]]
then
productioncommit+="$(echo "${seperaterevertcommit}" )";
productioncommit+=" ";
break;
fi
done 
done;
echo $productioncommit
movablecommit=" $(echo "${productioncommit[@]}" | tr ' ' '\n' | sort -u | tr '\n' ' ')";
for lastcommit in $movablecommit
do
  git checkout production
  git pull
  echo $lastcommit
  git checkout master
done;
fi;
echo "finished"
