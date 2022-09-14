git checkout master; 
git branch;
echo "Previous_Day_Merge_Commit";           
previousdaycommit=`git rev-list -1 --before="24 hour ago" master`;
echo $previousdaycommit;  
echo "Current_Day_Last_Commit";
currentdaycommit=`git rev-list -1 --since="24 hour" master`;
echo $currentdaycommit;
echo "In_Between_Commits";
inbetweencommit=`git rev-list $previousdaycommit..$currentdaycommit -m master --not production`; 
echo $inbetweencommit;
echo "Revert_Commit_For_Last_24_hours";
for allcommits in $inbetweencommit;
do
revertcommit=` git rev-list $allcommits --grep="Revert " $previousdaycommit..$currentdaycommit -m master --not production`; 
done;
echo $revertcommit;
if [[ $revertcommit ]]
then
echo "Actual_Commit_From_Revert_Commit";
for actualfromrevertcommit in $revertcommit;
do   
revertoriginalcommit=` git log $actualfromrevertcommit  $previousdaycommit..$currentdaycommit --format=%B | grep -oP '(?<=This reverts commit )[^ ]*' | tr -d '.'`;          
done;
echo $revertoriginalcommit
echo "Matched_Revert_Commit_For_The_Current_Day_Build";
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
echo "Original_Commit_Moves_To_The_Production";
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
fi;
done; 
done;
echo $productioncommit;
movablecommit=" $(echo "${productioncommit[@]}" | tr ' ' '\n' | sort -u | tr '\n' ' ')";
for lastcommit in $movablecommit
do
  git checkout production
  git fetch --all
  git pull
  git cherry-pick -x $lastcommit 
  git push -v --tags origin production
  git checkout master
done;
fi;
echo "Finished"; 